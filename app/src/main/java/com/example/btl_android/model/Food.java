package com.example.btl_android.model;

import java.io.Serializable;

public class Food implements Serializable {
    private String id;
    private String name;
    private String description;
    private int price;
    private String image;
    private String categoryId;
    private int calorie;
    private int quantity = 1;

    public Food() {
        // Dùng cho Firebase hoặc deserialize
    }

    public Food(String id, String name, String description, int price, String image, String categoryId, int calorie) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
        this.calorie = calorie;
        this.quantity = 1; // mặc định 1 khi khởi tạo
    }

    // Getter - Setter cho calorie
    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    // Getter - Setter cho quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Các getter - setter còn lại
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
