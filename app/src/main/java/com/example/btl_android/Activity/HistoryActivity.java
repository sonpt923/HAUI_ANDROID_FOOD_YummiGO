package com.example.btl_android.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.btl_android.Adapter.OrderAdapter;
import com.example.btl_android.databinding.ActivityHistoryBinding;
import com.example.btl_android.model.Order;
import com.example.btl_android.utils.TinyDB;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private ArrayList<Order> historyList;
    private TinyDB tinyDB;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tinyDB = new TinyDB(this);
        historyList = tinyDB.getListObject("HISTORY_LIST", Order.class);

        if (historyList == null) {
            historyList = new ArrayList<>();
        }
        // Đảo ngược danh sách để đơn hàng mới nhất lên đầu
      //  Collections.reverse(historyList);

        adapter = new OrderAdapter(historyList, this);
        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerHistory.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
