package com.example.btl_android.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.Adapter.FoodAdapter;
import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityFavoriteBinding;
import com.example.btl_android.model.Food;
import com.example.btl_android.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private ArrayList<Food> favoriteList;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.favoriteRecyclerView);
        tinyDB = new TinyDB(this);
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        loadFavoriteData();
    }

    private void loadFavoriteData() {
        favoriteList = tinyDB.getListObject("FAVORITE_LIST", Food.class);
        if (favoriteList == null) {
            favoriteList = new ArrayList<>();
        }

        adapter = new FoodAdapter(favoriteList, this); // Đảm bảo FoodAdapter có constructor này
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
    }
}
