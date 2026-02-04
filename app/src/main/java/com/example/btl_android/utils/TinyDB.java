package com.example.btl_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TinyDB {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public TinyDB(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        gson = new Gson();
    }

    // ===== Lưu & lấy String =====
    public void putString(String key, String value) {
        editor.putString(key, value).apply();
    }

    public String getString(String key) {
        return preferences.getString(key, null);
    }

    // ===== Lưu & lấy int =====
    public void putInt(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    // ===== Lưu & lấy boolean =====
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    // ===== Lưu & lấy float =====
    public void putFloat(String key, float value) {
        editor.putFloat(key, value).apply();
    }

    public float getFloat(String key) {
        return preferences.getFloat(key, 0f);
    }

    // ===== Lưu & lấy long =====
    public void putLong(String key, long value) {
        editor.putLong(key, value).apply();
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0L);
    }

    // ===== Lưu & lấy ArrayList<String> =====
    public void putListString(String key, ArrayList<String> list) {
        String json = gson.toJson(list);
        editor.putString(key, json).apply();
    }

    public ArrayList<String> getListString(String key) {
        String json = preferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // ===== Lưu & lấy danh sách object bất kỳ =====
    public <T> void putListObject(String key, ArrayList<T> list) {
        String json = gson.toJson(list);
        editor.putString(key, json).apply();
    }

    public <T> ArrayList<T> getListObject(String key, Class<T> clazz) {
        String json = preferences.getString(key, null);
        if (json == null) return null;

        Type type = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return gson.fromJson(json, type);
    }

    // ===== Xóa theo key =====
    public void remove(String key) {
        editor.remove(key).apply();
    }

    // ===== Xóa toàn bộ =====
    public void clear() {
        editor.clear().apply();
    }
}
