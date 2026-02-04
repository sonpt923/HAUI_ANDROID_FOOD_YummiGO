package com.example.btl_android.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.btl_android.Adapter.CartAdapter;
import com.example.btl_android.databinding.ActivityCartBinding;
import com.example.btl_android.model.Food;
import com.example.btl_android.utils.TinyDB;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    private ArrayList<Food> cartList;
    private CartAdapter cartAdapter;
    private TinyDB tinyDB;

    private final int SHIP_FEE = 15000;
    private final int TAX_FEE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo TinyDB và lấy danh sách giỏ hàng
        tinyDB = new TinyDB(this);
        cartList = tinyDB.getListObject("CART_LIST", Food.class);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        // Setup RecyclerView
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartList, this, this::updateTotal);
        binding.cartRecyclerView.setAdapter(cartAdapter);

        // Hiển thị tổng tiền ban đầu
        updateTotal();

        // Nút quay lại
        binding.btnBack.setOnClickListener(v -> finish());

        // Nút thanh toán
        binding.btnThanhtoan.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng điền thêm thông tin để đặt hàng!", Toast.LENGTH_LONG).show();

                // Chuyển sang màn PlaceOrderActivity và truyền danh sách giỏ hàng
                Intent intent = new Intent(CartActivity.this, PlaceOrderActivity.class);
                tinyDB.putListObject("ORDER_LIST", cartList); // để PlaceOrderActivity đọc
                startActivity(intent);
            }
        });
    }

    // Cập nhật tổng chi phí
    private void updateTotal() {
        int tamTinh = 0;
        for (Food item : cartList) {
            tamTinh += item.getPrice() * item.getQuantity();
        }

        int tong = tamTinh + SHIP_FEE + TAX_FEE;

        // Cập nhật giao diện
        binding.txtTamTinh.setText("Tạm tính: " + tamTinh + " VND");
        binding.txtShip.setText("Ship: " + SHIP_FEE + " VND");
        binding.txtThue.setText("Thuế: " + TAX_FEE + " VND");
        binding.txtTotal.setText("Tổng: " + tong + " VND");

        // Cập nhật lại TinyDB
        tinyDB.putListObject("CART_LIST", cartList);
    }
}
