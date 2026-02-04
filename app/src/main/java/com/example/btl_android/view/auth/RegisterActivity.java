package com.example.btl_android.view.auth;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.example.btl_android.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword, edtPasswordAgain;
    private ImageButton btnShowPassword, btnShowPasswordAgain;
    private Button btnRegister;
    private TextView tvLoginRedirect;

    private FirebaseAuth mAuth;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordAgain = findViewById(R.id.edtPassword_again);
        btnShowPassword = findViewById(R.id.btn_showPassword);
        btnShowPasswordAgain = findViewById(R.id.btn_showPassword_again);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        // Toggle hiển/ẩn mật khẩu
        btnShowPassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            togglePasswordVisibility(edtPassword, btnShowPassword, isPasswordVisible);
        });

        btnShowPasswordAgain.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            togglePasswordVisibility(edtPasswordAgain, btnShowPasswordAgain, isConfirmPasswordVisible);
        });

        // Sự kiện đăng ký
        btnRegister.setOnClickListener(v -> registerUser());

        // Chuyển sang LoginActivity khi click "Đăng nhập"
        String fullText = "Bạn đã có tài khoản YummiGO? Đăng nhập";
        SpannableString ss = new SpannableString(fullText);
        int start = fullText.indexOf("Đăng nhập");
        int end = start + "Đăng nhập".length();
        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#0077CC"));
                ds.setUnderlineText(false);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLoginRedirect.setText(ss);
        tvLoginRedirect.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void togglePasswordVisibility(EditText editText, ImageButton button, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            button.setAlpha(1.0f);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            button.setAlpha(0.3f);
        }
        editText.setSelection(editText.getText().length());
    }

    private void registerUser() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtPasswordAgain.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Vui lòng nhập email");
            edtUsername.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            edtUsername.setError("Email không hợp lệ");
            edtUsername.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            edtPassword.setError("Mật khẩu phải ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            edtPasswordAgain.setError("Mật khẩu không khớp");
            edtPasswordAgain.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Lưu thêm thông tin user vào Realtime Database
                        String uid = mAuth.getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(uid)
                                .setValue(new UserModel(username))
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Lỗi lưu dữ liệu", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}



