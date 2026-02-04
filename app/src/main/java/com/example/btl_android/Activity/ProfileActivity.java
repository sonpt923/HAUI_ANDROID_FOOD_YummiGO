package com.example.btl_android.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityProfileBinding;
import com.example.btl_android.model.UserModel;
import com.example.btl_android.utils.TinyDB;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    TinyDB tinyDB;
    UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tinyDB = new TinyDB(this);
        loadUserData();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSaveProfile.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        ArrayList<UserModel> users = tinyDB.getListObject("USER_DATA", UserModel.class);
        
        if (users != null && !users.isEmpty()) {
            currentUser = users.get(0);
            binding.edtEmail.setText(currentUser.getEmail());
            if (currentUser.getLength() != null) binding.edtHeight.setText(String.valueOf(currentUser.getLength()));
            if (currentUser.getWeight() != null) binding.edtWeight.setText(String.valueOf(currentUser.getWeight()));
            if (currentUser.getAge() != null) binding.edtAge.setText(String.valueOf(currentUser.getAge()));
            
            if ("Nam".equals(currentUser.getGender())) {
                binding.rbMale.setChecked(true);
            } else if ("Nữ".equals(currentUser.getGender())) {
                binding.rbFemale.setChecked(true);
            }
        } else {
            currentUser = new UserModel("user@example.com");
            binding.edtEmail.setText(currentUser.getEmail());
        }
    }

    private void saveUserData() {
        String heightStr = binding.edtHeight.getText().toString().trim();
        String weightStr = binding.edtWeight.getText().toString().trim();
        String ageStr = binding.edtAge.getText().toString().trim();
        
        String gender = "";
        if (binding.rbMale.isChecked()) {
            gender = "Nam";
        } else if (binding.rbFemale.isChecked()) {
            gender = "Nữ";
        }

        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            currentUser.setLength(Integer.parseInt(heightStr));
            currentUser.setWeight(Integer.parseInt(weightStr));
            currentUser.setAge(Integer.parseInt(ageStr));
            currentUser.setGender(gender);

            ArrayList<UserModel> users = new ArrayList<>();
            users.add(currentUser);
            tinyDB.putListObject("USER_DATA", users);

            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}
