package com.example.btl_android.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.btl_android.Adapter.PlaceOrderAdapter;
import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityPlaceOrderBinding;
import com.example.btl_android.model.Food;
import com.example.btl_android.model.Order;
import com.example.btl_android.utils.PayOSHelper;
import com.example.btl_android.utils.TinyDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PlaceOrderActivity extends BaseActivity {

    ActivityPlaceOrderBinding binding;
    TinyDB tinyDB;
    ArrayList<Food> cartList;
    PlaceOrderAdapter adapter;
    PayOSHelper payOSHelper;

    int totalAmount = 0;
    int shippingFee = 15000;
    int tax = 0;
    long currentOrderCode = 0;
    boolean isQRReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tinyDB = new TinyDB(this);
        payOSHelper = new PayOSHelper();
        cartList = tinyDB.getListObject("CART_LIST", Food.class);

        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupCartList();
        calculateTotal();
        handlePaymentSelection();
        handlePlaceOrder();

        binding.btnRefreshQR.setOnClickListener(v -> requestPayOSLink());
    }

    private void setupCartList() {
        adapter = new PlaceOrderAdapter(this, cartList);
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCart.setAdapter(adapter);
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (Food food : cartList) {
            totalAmount += food.getPrice() * food.getQuantity();
        }

        binding.tvSubtotal.setText(String.format(Locale.getDefault(), "%d VND", totalAmount));
        binding.tvShipping.setText(String.format(Locale.getDefault(), "%d VND", shippingFee));

        int finalTotal = totalAmount + shippingFee + tax;
        binding.tvTotal.setText(String.format(Locale.getDefault(), "%d VND", finalTotal));
    }

    private void handlePaymentSelection() {
        binding.rbTTnh.setChecked(true);
        binding.layoutQR.setVisibility(View.GONE);

        binding.rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTTck) {
                binding.layoutQR.setVisibility(View.VISIBLE);
                requestPayOSLink();
            } else {
                binding.layoutQR.setVisibility(View.GONE);
                isQRReady = false;
            }
        });
    }

    private void requestPayOSLink() {
        isQRReady = false;
        runOnUiThread(() -> {
            binding.pbLoadingQR.setVisibility(View.VISIBLE);
            binding.btnRefreshQR.setVisibility(View.GONE);
            binding.qrImage.setAlpha(0.3f);
        });

        int finalTotal = totalAmount + shippingFee + tax;
        currentOrderCode = System.currentTimeMillis() / 1000;

        try {
            JSONObject body = new JSONObject();
            body.put("orderCode", currentOrderCode);
            body.put("amount", finalTotal);
            body.put("description", "YummiGO Order " + currentOrderCode);
            body.put("cancelUrl", "https://yourdomain.com/cancel");
            body.put("returnUrl", "https://yourdomain.com/success");

            payOSHelper.createPaymentLink(body, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showQRError();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String jsonData = response.body().string();
                            JSONObject resObj = new JSONObject(jsonData);
                            JSONObject data = resObj.getJSONObject("data");
                            
                            // Chuỗi QR thô từ PayOS
                            String rawQrCode = data.getString("qrCode");

                            // Chuyển chuỗi thô thành URL ảnh QR
                            String qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=" 
                                    + URLEncoder.encode(rawQrCode, "UTF-8");

                            runOnUiThread(() -> {
                                Glide.with(PlaceOrderActivity.this)
                                        .load(qrImageUrl)
                                        .into(binding.qrImage);
                                binding.qrImage.setAlpha(1.0f);
                                binding.pbLoadingQR.setVisibility(View.GONE);
                                isQRReady = true;
                            });
                        } catch (Exception e) {
                            showQRError();
                        }
                    } else {
                        showQRError();
                    }
                }
            });
        } catch (JSONException e) {
            showQRError();
        }
    }

    private void showQRError() {
        runOnUiThread(() -> {
            binding.pbLoadingQR.setVisibility(View.GONE);
            binding.btnRefreshQR.setVisibility(View.VISIBLE);
            Toast.makeText(PlaceOrderActivity.this, "Không thể lấy mã QR. Nhấn nút để thử lại.", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkPaymentStatus(PaymentStatusCallback callback) {
        payOSHelper.checkPayment(currentOrderCode, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onResult(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject resObj = new JSONObject(jsonData);
                        JSONObject data = resObj.getJSONObject("data");
                        String status = data.getString("status");
                        callback.onResult("PAID".equals(status));
                    } catch (Exception e) {
                        callback.onResult(false);
                    }
                } else {
                    callback.onResult(false);
                }
            }
        });
    }

    interface PaymentStatusCallback {
        void onResult(boolean isPaid);
    }

    private void handlePlaceOrder() {
        binding.btnPlaceOrder.setOnClickListener(v -> {
            String name = binding.edtName.getText().toString().trim();
            String address = binding.edtAddress.getText().toString().trim();
            String phone = binding.edtPhone.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.rbTTck.isChecked()) {
                if (!isQRReady) {
                    Toast.makeText(this, "Vui lòng đợi mã QR hiển thị hoặc thử lại", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.btnPlaceOrder.setEnabled(false);
                binding.btnPlaceOrder.setText("Đang kiểm tra thanh toán...");

                checkPaymentStatus(isPaid -> runOnUiThread(() -> {
                    binding.btnPlaceOrder.setEnabled(true);
                    binding.btnPlaceOrder.setText("Đặt hàng");
                    if (isPaid) {
                        processOrder("Chuyển khoản (Đã xác nhận)");
                    } else {
                        Toast.makeText(PlaceOrderActivity.this, "Chưa nhận được thanh toán. Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                    }
                }));
            } else {
                processOrder("Thanh toán khi nhận");
            }
        });
    }

    private void processOrder(String paymentMethod) {
        int finalTotal = totalAmount + shippingFee + tax;
        String orderId = "DH" + (currentOrderCode > 0 ? currentOrderCode : System.currentTimeMillis());

        Order newOrder = new Order(orderId,
                binding.edtName.getText().toString().trim(),
                binding.edtAddress.getText().toString().trim(),
                binding.edtPhone.getText().toString().trim(),
                paymentMethod, finalTotal, new ArrayList<>(cartList));

        ArrayList<Order> historyList = tinyDB.getListObject("HISTORY_LIST", Order.class);
        if (historyList == null) historyList = new ArrayList<>();
        historyList.add(0, newOrder);
        tinyDB.putListObject("HISTORY_LIST", historyList);

        tinyDB.remove("CART_LIST");
        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
        startActivity(new Intent(PlaceOrderActivity.this, ThankYouActivity.class));
        finish();
    }
}
