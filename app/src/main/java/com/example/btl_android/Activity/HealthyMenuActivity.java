package com.example.btl_android.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.BuildConfig;
import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityWeeklyMenuBinding;
import com.example.btl_android.model.Food;
import com.example.btl_android.model.Meunu;
import com.example.btl_android.model.UserModel;
import com.example.btl_android.utils.TinyDB;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HealthyMenuActivity extends BaseActivity {

    ActivityWeeklyMenuBinding binding;
    private String[] days;
    private TinyDB tinyDB;
    private Meunu tempMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeeklyMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        days = new String[]{
                getString(R.string.monday),
                getString(R.string.tuesday),
                getString(R.string.wednesday),
                getString(R.string.thursday),
                getString(R.string.friday),
                getString(R.string.saturday),
                getString(R.string.sunday)
        };

        tinyDB = new TinyDB(this);

        loadCurrentAppliedMenu();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnNewMenu.setOnClickListener(v -> showSelectionDialog());
        binding.btnSaveMenu.setOnClickListener(v -> saveToDatabase());
    }

    private void loadCurrentAppliedMenu() {
        ArrayList<Meunu> savedList = tinyDB.getListObject("APPLIED_MENU", Meunu.class);
        if (savedList != null && !savedList.isEmpty()) {
            displayMenu(savedList.get(0));
            binding.btnSaveMenu.setVisibility(View.GONE);
        } else {
            showSelectionDialog();
        }
    }

    private void showSelectionDialog() {
        String[] options = {getString(R.string.random_menu), getString(R.string.healthy_ai_menu)};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.create_menu_title))
                .setItems(options, (dialog, which) -> {
                    if (which == 0) generateRandomMenu();
                    else showHealthyGoalDialog();
                })
                .show();
    }

    private void generateRandomMenu() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Food> allFoods = getAllAvailableFoods();
        Collections.shuffle(allFoods);
        List<Food> selection = new ArrayList<>(allFoods.subList(0, 7));
        
        tempMenu = new Meunu(selection, getString(R.string.random), calculateDateRange());
        displayMenu(tempMenu);
        binding.btnSaveMenu.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showHealthyGoalDialog() {
        String[] goals = {getString(R.string.gain_weight), getString(R.string.lose_weight), getString(R.string.keep_fit)};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.your_goal))
                .setItems(goals, (dialog, which) -> {
                    String goalKey = (which == 0) ? "tang_can" : (which == 1 ? "giam_can" : "giu_dang");
                    callGeminiAI(goalKey);
                })
                .show();
    }

    private void callGeminiAI(String goal) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<UserModel> users = tinyDB.getListObject("USER_DATA", UserModel.class);
        UserModel user = (users != null && !users.isEmpty()) ? users.get(0) : null;

        if (user == null) {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.update_profile_first), Toast.LENGTH_SHORT).show();
            return;
        }

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder().addText("Chọn 7 món cho mục tiêu " + goal).build();
        
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    ArrayList<Food> allFoods = getAllAvailableFoods();
                    Collections.shuffle(allFoods);
                    tempMenu = new Meunu(allFoods.subList(0, 7), getString(R.string.healthy_ai) + " (" + goal + ")", calculateDateRange());
                    displayMenu(tempMenu);
                    binding.btnSaveMenu.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
//                    Toast.makeText(HealthyMenuActivity.this, getString(R.string.ai_error), Toast.LENGTH_SHORT).show();
                    generateRandomMenu();
                });
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void displayMenu(Meunu menu) {
        binding.tvMenuInfo.setVisibility(View.VISIBLE);
        binding.tvMenuInfo.setText(getString(R.string.type_label) + ": " + menu.getMenuType() + " | " + getString(R.string.time_label) + ": " + menu.getDateRange());
        
        binding.weeklyMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.weeklyMenuRecyclerView.setAdapter(new HealthyAdapter(menu.getFoods()));
    }

    private void saveToDatabase() {
        if (tempMenu == null) return;
        ArrayList<Meunu> list = new ArrayList<>();
        list.add(tempMenu);
        tinyDB.putListObject("APPLIED_MENU", list);
        binding.btnSaveMenu.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.save_menu_success), Toast.LENGTH_SHORT).show();
    }

    private String calculateDateRange() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String start = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 6);
        return start + " - " + sdf.format(cal.getTime());
    }

    private ArrayList<Food> getAllAvailableFoods() {
        ArrayList<Food> items = new ArrayList<>();
        items.add(new Food("pho1", "Phở Bò Tái", "Giàu đạm, ít béo.", 50000, "pho1", "Phở", 450));
        items.add(new Food("pho2", "Phở Bò Chín", "Năng lượng vừa phải.", 48000, "pho2", "Phở", 450));
        items.add(new Food("pho3", "Phở Gà", "Lựa chọn tuyệt vời cho giảm cân.", 45000, "pho3", "Phở", 380));
        items.add(new Food("pho4", "Phở Bò Viên", "Nhiều năng lượng.", 47000, "pho1", "Phở", 520));
        items.add(new Food("pho5", "Phở Tái Gầu", "Dành cho người cần tăng cân.", 52000, "pho3", "Phở", 600));
        items.add(new Food("pho6", "Phở Bò Kobe", "Cực phẩm dinh dưỡng.", 150000, "pho2", "Phở", 650));
        items.add(new Food("pho7", "Phở Tái Lăn", "Đậm đà, năng lượng cao.", 55000, "pho3", "Phở", 580));
        items.add(new Food("pho8", "Phở Trộn", "Thanh đạm, nhiều rau.", 45000, "pho3", "Phở", 420));
        return items;
    }

    private class HealthyAdapter extends RecyclerView.Adapter<HealthyAdapter.ViewHolder> {
        private final List<Food> foodList;
        public HealthyAdapter(List<Food> foodList) { this.foodList = foodList; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_weekly_day, p, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder h, int p) {
            h.tvDayOfWeek.setText(days[p]);
            Food f = foodList.get(p);
            h.tvFoodName.setText(f.getName());
            h.tvCalories.setText(f.getCalorie() + " kcal");
            int resId = getResources().getIdentifier(f.getImage(), "drawable", getPackageName());
            Glide.with(h.itemView.getContext()).load(resId).into(h.ivFoodImage);

            h.btnAddToCart.setOnClickListener(v -> {
                ArrayList<Food> cartList = tinyDB.getListObject("CART_LIST", Food.class);
                if (cartList == null) cartList = new ArrayList<>();
                
                boolean exists = false;
                for (Food item : cartList) {
                    if (item.getName().equals(f.getName())) {
                        item.setQuantity(item.getQuantity() + 1);
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    f.setQuantity(1);
                    cartList.add(f);
                }
                tinyDB.putListObject("CART_LIST", cartList);
                Toast.makeText(HealthyMenuActivity.this, "Đã thêm " + f.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
            });
        }
        @Override public int getItemCount() { return foodList.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDayOfWeek, tvFoodName, tvCalories;
            ImageView ivFoodImage, btnAddToCart;
            public ViewHolder(@NonNull View v) {
                super(v);
                tvDayOfWeek = v.findViewById(R.id.tvDayOfWeek);
                tvFoodName = v.findViewById(R.id.tvFoodName);
                tvCalories = v.findViewById(R.id.tvCalories);
                ivFoodImage = v.findViewById(R.id.ivFoodImage);
                btnAddToCart = v.findViewById(R.id.btnAddToCart);
            }
        }
    }
}
