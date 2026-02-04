package com.example.btl_android.Activity;

import com.example.btl_android.model.Food;

import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static FavoriteManager instance;
    private List<Food> favoriteList = new ArrayList<>();

    private FavoriteManager() {}

    public static FavoriteManager getInstance() {
        if (instance == null) {
            instance = new FavoriteManager();
        }
        return instance;
    }

    public void addFavorite(Food food) {
        if (!favoriteList.contains(food)) {
            favoriteList.add(food);
        }
    }

    public void removeFavorite(Food food) {
        favoriteList.remove(food);
    }

    public List<Food> getFavoriteList() {
        return favoriteList;
    }

    public boolean isFavorite(Food food) {
        return favoriteList.contains(food);
    }
}
