package com.example.btl_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.databinding.ItemPlaceOrderBinding;
import com.example.btl_android.model.Food;

import java.util.List;

public class PlaceOrderAdapter extends RecyclerView.Adapter<PlaceOrderAdapter.PlaceOrderViewHolder> {

    private Context context;
    private List<Food> foodList;

    public PlaceOrderAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public PlaceOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaceOrderBinding binding = ItemPlaceOrderBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new PlaceOrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceOrderViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.binding.tvFoodName.setText(food.getName());
        holder.binding.tvFoodDescription.setText(food.getDescription());
        holder.binding.tvFoodQuantity.setText("Số lượng: " + food.getQuantity());
        holder.binding.tvFoodPrice.setText(food.getPrice() + "VND");

        Glide.with(context)
                .load(food.getImage())
                .into(holder.binding.imgFood);
    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public class PlaceOrderViewHolder extends RecyclerView.ViewHolder {
        ItemPlaceOrderBinding binding;

        public PlaceOrderViewHolder(@NonNull ItemPlaceOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
