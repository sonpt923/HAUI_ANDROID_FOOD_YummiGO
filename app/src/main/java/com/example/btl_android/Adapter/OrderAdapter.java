package com.example.btl_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_android.databinding.ItemOrderBinding;
import com.example.btl_android.model.Food;
import com.example.btl_android.model.Order;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private ArrayList<Order> orderList;
    private Context context;

    public OrderAdapter(ArrayList<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(context), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.binding.tvOrderId.setText("Mã đơn: " + order.getId());
        holder.binding.tvName.setText("Người đặt: " + order.getName());
        holder.binding.tvAddress.setText("Địa chỉ: " + order.getAddress());
        holder.binding.tvPhone.setText("SĐT: " + order.getPhone());
        holder.binding.tvPayment.setText("Thanh toán: " + order.getPaymentMethod());
        holder.binding.tvTotal.setText("Tổng: " + order.getTotalAmount() + " VND");

        StringBuilder items = new StringBuilder();
        if(order.getItems() != null && !order.getItems().isEmpty()){
            for (Food f : order.getItems()) {
                items.append("- ").append(f.getName())
                        .append(" x").append(f.getQuantity())
                        .append("\n");
            }
        }else {
            items.append("Không có món nào");
        }


        holder.binding.tvItems.setText(items.toString().trim());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;

        public OrderViewHolder(@NonNull ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

