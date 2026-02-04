package com.example.btl_android.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityDetailBinding;
import com.example.btl_android.model.Food;
import com.example.btl_android.utils.TinyDB;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private Food object;
    private int num = 1;
    private boolean isFavorite = false;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tinyDB = new TinyDB(this);

        getIntentExtra();
        setVariable();
        handleCartIconClick();
        handleHomeIconClick();
        setupFavorite();
    }

    // Nhận dữ liệu từ Intent
    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null && intent.getSerializableExtra("object") != null) {
            object = (Food) intent.getSerializableExtra("object");
        }
    }

    // Thiết lập thông tin hiển thị và xử lý tương tác
    private void setVariable() {
        if (object == null) return;

        binding.btnBack.setOnClickListener(v -> finish());

        int imageResId = getResources().getIdentifier(object.getImage(), "drawable", getPackageName());
        Glide.with(this)
                .load(imageResId)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.pic1);

        binding.txtTitleChitiet.setText(object.getName());
        binding.txtDetail.setText(object.getDescription());
        binding.txtPrice.setText(object.getPrice() + " VND");
        binding.txtQuantity.setText(String.valueOf(num));
        binding.Tongtien.setText(num * object.getPrice() + " VND");
        binding.rattingbar.setRating(4); // Default rating
        
        // Hiển thị Calorie
        binding.txtCalories.setText(object.getCalorie() + " kcal");

        // Tăng số lượng
        binding.btnPlus.setOnClickListener(v -> {
            num++;
            binding.txtQuantity.setText(String.valueOf(num));
            binding.Tongtien.setText(num * object.getPrice() + " VND");
        });

        // Giảm số lượng
        binding.btnMinus.setOnClickListener(v -> {
            if (num > 1) {
                num--;
                binding.txtQuantity.setText(String.valueOf(num));
                binding.Tongtien.setText(num * object.getPrice() + " VND");
            }
        });

        // Thêm vào giỏ hàng
        binding.btnAdd.setOnClickListener(v -> {
            object.setQuantity(num);

            ArrayList<Food> cartList = tinyDB.getListObject("CART_LIST", Food.class);
            if (cartList == null) cartList = new ArrayList<>();

            boolean found = false;
            for (Food f : cartList) {
                if (f.getId().equals(object.getId())) {
                    f.setQuantity(f.getQuantity() + num);
                    found = true;
                    break;
                }
            }

            if (!found) {
                cartList.add(object);
            }

            tinyDB.putListObject("CART_LIST", cartList);

            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    // Xử lý nút về trang chủ
    private void handleHomeIconClick() {
        binding.home.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Xử lý nút mở giỏ hàng
    private void handleCartIconClick() {
        binding.giohang.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    // ======================= YÊU THÍCH ===========================

    private void setupFavorite() {
        ImageView btnFavorite = binding.btnFavorite;

        ArrayList<Food> tempList = tinyDB.getListObject("FAVORITE_LIST", Food.class);
        if (tempList == null) {
            tempList = new ArrayList<>();
        }
        final ArrayList<Food> favoriteList = tempList;


        for (Food f : favoriteList) {
            if (f.getId().equals(object.getId())) {
                isFavorite = true;
                break;
            }
        }

        updateFavoriteIcon(btnFavorite);

        btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteIcon(btnFavorite);

            if (isFavorite) {
                favoriteList.add(object);
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < favoriteList.size(); i++) {
                    if (favoriteList.get(i).getId().equals(object.getId())) {
                        favoriteList.remove(i);
                        break;
                    }
                }
                Toast.makeText(this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
            }

            tinyDB.putListObject("FAVORITE_LIST", favoriteList);
            Log.d("FAVORITE_CLICK", "isFavorite = " + isFavorite);
        });
    }

    private void updateFavoriteIcon(ImageView btnFavorite) {
        btnFavorite.setImageResource(
                isFavorite ? R.drawable.favorite_red : R.drawable.favorite_white
        );
    }

}
