package com.example.btl_android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String paymentMethod;
    private int totalAmount;
    private ArrayList<Food> items;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(String id, String name, String address, String phone, String paymentMethod, int totalAmount, ArrayList<Food> items) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public void setItems(ArrayList<Food> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    // Getters và setters

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getPaymentMethod() { return paymentMethod; }
    public int getTotalAmount() { return totalAmount; }
    public ArrayList<Food> getItems() { return items; }
}
