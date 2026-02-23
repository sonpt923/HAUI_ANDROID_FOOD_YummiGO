package com.example.btl_android.model;

import java.io.Serializable;
import java.util.List;

public class Meunu implements Serializable {
    private List<Food> foods;
    private String menuType;
    private String dateRange;

    public Meunu(List<Food> foods, String menuType, String dateRange) {
        this.foods = foods;
        this.menuType = menuType;
        this.dateRange = dateRange;
    }

    public List<Food> getFoods() { return foods; }
    public String getMenuType() { return menuType; }
    public String getDateRange() { return dateRange; }
}
