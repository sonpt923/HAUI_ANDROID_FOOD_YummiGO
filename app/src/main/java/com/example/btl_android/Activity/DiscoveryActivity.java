package com.example.btl_android.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_android.databinding.ActivityDiscoveryBinding;

public class DiscoveryActivity extends AppCompatActivity {

    ActivityDiscoveryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiscoveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Chỉ một chức năng Thực đơn chính
        binding.cardWeeklyMenu.setOnClickListener(v -> {
            Intent intent = new Intent(DiscoveryActivity.this, HealthyMenuActivity.class);
            startActivity(intent);
        });
        
        // Ẩn thẻ không sử dụng
        binding.cardHealthyMenu.setVisibility(android.view.View.GONE);
    }
}
