package com.example.btl_android.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityWeeklyMenuBinding;
import com.example.btl_android.model.Food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeeklyMenuActivity extends AppCompatActivity {

    ActivityWeeklyMenuBinding binding;
    private final String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
    private WeeklyMenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeeklyMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        binding.weeklyMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        generateWeeklyMenu();
    }

    private void generateWeeklyMenu() {
        ArrayList<Food> allFoods = getAllAvailableFoods();
        Collections.shuffle(allFoods);
        List<Food> weeklySelection = allFoods.subList(0, Math.min(7, allFoods.size()));

        adapter = new WeeklyMenuAdapter(weeklySelection);
        binding.weeklyMenuRecyclerView.setAdapter(adapter);
    }

    private ArrayList<Food> getAllAvailableFoods() {
        ArrayList<Food> items = new ArrayList<>();
        items.add(new Food("pho1", "Phở Bò Tái", "Phở bò tái với nước dùng truyền thống.", 50000, "pho1", "Phở", 450));
        items.add(new Food("pho2", "Phở Bò Chín", "Phở bò chín mềm thơm, chuẩn vị Hà Nội.", 48000, "pho2", "Phở", 450));
        items.add(new Food("pho3", "Phở Gà", "Phở gà ta dai ngon, nước dùng trong.", 45000, "pho3", "Phở", 400));
        items.add(new Food("pho4", "Phở Bò Viên", "Phở bò viên kèm rau sống đầy đủ.", 47000, "pho1", "Phở", 480));
        items.add(new Food("pho5", "Phở Tái Gầu", "Tái gầu béo ngậy hòa quyện nước dùng.", 52000, "pho3", "Phở", 550));
        items.add(new Food("pho6", "Phở Bò Kobe", "Thịt bò Kobe nhập khẩu cao cấp.", 150000, "pho2", "Phở", 600));
        items.add(new Food("pho7", "Phở Tái Lăn", "Thịt bò tái xào thơm trước khi chan nước.", 55000, "pho3", "Phở", 500));
        items.add(new Food("pho8", "Phở Trộn", "Phở trộn không nước, kèm rau và nước sốt.", 45000, "pho3", "Phở", 450));
        
        while (items.size() < 7) {
            items.addAll(new ArrayList<>(items));
        }
        return items;
    }

    private class WeeklyMenuAdapter extends RecyclerView.Adapter<WeeklyMenuAdapter.ViewHolder> {
        private final List<Food> foodList;

        public WeeklyMenuAdapter(List<Food> foodList) {
            this.foodList = foodList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_day, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvDayOfWeek.setText(days[position]);
            Food food = foodList.get(position);
            holder.tvFoodName.setText(food.getName());
            holder.tvCalories.setText(food.getCalorie() + " kcal");
            
            int imageResId = getResources().getIdentifier(food.getImage(), "drawable", getPackageName());
            Glide.with(holder.itemView.getContext())
                    .load(imageResId)
                    .into(holder.ivFoodImage);
        }

        @Override
        public int getItemCount() {
            return 7;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDayOfWeek, tvFoodName, tvCalories;
            ImageView ivFoodImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
                tvFoodName = itemView.findViewById(R.id.tvFoodName);
                tvCalories = itemView.findViewById(R.id.tvCalories);
                ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            }
        }
    }
}
