package com.example.btl_android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_android.Activity.DetailActivity;
import com.example.btl_android.R;
import com.example.btl_android.databinding.ViewholderPupListBinding;
import com.example.btl_android.model.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    ArrayList<Food> items;
    Context context;

    public FoodAdapter(ArrayList<Food> items){
        this.items=items;
    }
    public FoodAdapter(ArrayList<Food> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      context = parent.getContext();
      ViewholderPupListBinding binding = ViewholderPupListBinding.inflate(LayoutInflater. from(context), parent, false);
      return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolder holder, int position) {
        Food food = items.get(position);
        holder.binding.titleTxt.setText(items.get(position).getName());
       holder.binding.titleQuan.setText(items.get(position).getDescription());
       holder.binding.gia.setText(items.get(position).getPrice()+ "VND");

       //log du lieu
        Log.d("FoodAdapter", "Name: " + food.getName() +
                ", Desc: " + food.getDescription() +
                ", Price: " + food.getPrice());


        //Load ảnh từ drawable theo tên:
        int imageResId = context.getResources().getIdentifier(items.get(position).getImage(), "drawable", context.getPackageName());

        Glide.with(context)
                .load(imageResId)
                .into(holder.binding.pic); // pic là ID của ImageView trong layout

        // detail
        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", food);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ViewholderPupListBinding binding;

        public ViewHolder(ViewholderPupListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
