package com.example.btl_android.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.btl_android.Adapter.PlaceOrderAdapter;
import com.example.btl_android.databinding.ActivityPlaceOrderBinding;
import com.example.btl_android.model.Food;
import com.example.btl_android.model.Order;
import com.example.btl_android.utils.TinyDB;

import java.util.ArrayList;

public class PlaceOrderActivity extends AppCompatActivity {

    ActivityPlaceOrderBinding binding;
    TinyDB tinyDB;
    ArrayList<Food> cartList;
    PlaceOrderAdapter adapter;

    int totalAmount = 0;
    int shippingFee = 15000;
    int tax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tinyDB = new TinyDB(this);
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

      //  tax = 0;  // Nếu muốn tính thêm thuế: tax = totalAmount * 10 / 100;

        binding.tvSubtotal.setText(totalAmount + " VND");
        binding.tvShipping.setText(shippingFee + " VND");

        int finalTotal = totalAmount + shippingFee + tax;
        binding.tvTotal.setText(finalTotal + " VND");
    }

    private void handlePaymentSelection() {
        // mặc định chọn thanh toán khi nhận hàng
        binding.rbTTnh.setChecked(true);

        binding.rbTTnh.setOnClickListener(v -> binding.qrImage.setVisibility(android.view.View.GONE));
        binding.rbTTck.setOnClickListener(v -> binding.qrImage.setVisibility(android.view.View.VISIBLE));
    }

    private void handlePlaceOrder() {
        binding.btnPlaceOrder.setOnClickListener(v -> {
            String name = binding.edtName.getText().toString().trim();
            String address = binding.edtAddress.getText().toString().trim();
            String phone = binding.edtPhone.getText().toString().trim();
            String coupon = binding.edtCoupon.getText().toString().trim();

            // ====== KIỂM TRA DỮ LIỆU ======
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("\\d{9,15}")) {
                Toast.makeText(this, "Số điện thoại phải có từ 9 đến 15 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!binding.rbTTnh.isChecked() && !binding.rbTTck.isChecked()) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            // ====== LƯU ĐƠN HÀNG ======
            String paymentMethod = binding.rbTTck.isChecked() ? "Chuyển khoản" : "Thanh toán khi nhận";
            int finalTotal = totalAmount + shippingFee + tax;
            String orderId = "DH" + System.currentTimeMillis();

            Order newOrder = new Order(orderId, name, address, phone, paymentMethod, finalTotal, new ArrayList<>(cartList));

            ArrayList<Order> historyList = tinyDB.getListObject("HISTORY_LIST", Order.class);
            if (historyList == null) {
                historyList = new ArrayList<>();
            }

            // Thêm đơn mới lên đầu danh sách
            historyList.add(0, newOrder);

            // Lưu vào TinyDB
            tinyDB.putListObject("HISTORY_LIST", historyList);

            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
            tinyDB.remove("CART_LIST");

            Intent intent = new Intent(PlaceOrderActivity.this, ThankYouActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
