package com.example.btl_android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.Activity.DetailActivity;
import com.example.btl_android.R;
import com.example.btl_android.databinding.ItemFoodBinding;
import com.example.btl_android.model.Food;

import java.util.List;

public class ListFoodAdapter extends RecyclerView.Adapter<ListFoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    private Context context;

    public ListFoodAdapter(List<Food> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemFoodBinding binding = ItemFoodBinding.inflate(inflater, parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.binding.txtName.setText(food.getName());
        holder.binding.txtDescription.setText(food.getDescription());
        holder.binding.txtPrice.setText(String.format("Giá: %.0f VND", food.getPrice()));

        Glide.with(context)
                .load(food.getImage()) // URL ảnh từ Firebase
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.binding.imgFood);

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", food);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        ItemFoodBinding binding;

        public FoodViewHolder(@NonNull ItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

