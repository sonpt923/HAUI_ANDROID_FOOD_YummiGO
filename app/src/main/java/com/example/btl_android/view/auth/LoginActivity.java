package com.example.btl_android.view.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.Activity.MainActivity;
import com.example.btl_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvQuenMK, tvLoginRedirect;
    private ImageButton btnShowPassword;

    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvQuenMK = findViewById(R.id.tvQuenMK);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);
        btnShowPassword = findViewById(R.id.btn_showPassword);

        // Xử lý hiện/ẩn mật khẩu
        btnShowPassword.setOnClickListener(view -> {
            if (isPasswordVisible) {
                edtPassword.setInputType(129); // textPassword
                btnShowPassword.setAlpha(0.3f);
            } else {
                edtPassword.setInputType(1); // textVisiblePassword
                btnShowPassword.setAlpha(1f);
            }
            edtPassword.setSelection(edtPassword.getText().length()); // Di chuyển con trỏ về cuối
            isPasswordVisible = !isPasswordVisible;
        });

        // Xử lý đăng nhập
        btnLogin.setOnClickListener(view -> {
            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                edtUsername.setError("Vui lòng nhập email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                            // Chuyển sang màn hình chính
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
        // Quên mật khẩu
        tvQuenMK.setOnClickListener(view -> {
            String email = edtUsername.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập email trước", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("email_key", email);
            startActivity(intent);
        });





        // Tạo chữ "Đăng ký" màu xanh và có thể click
        String fullText = "Bạn chưa có tài khoản YummiGO? Đăng ký";
        SpannableString ss = new SpannableString(fullText);
        int start = fullText.indexOf("Đăng ký");
        int end = start + "Đăng ký".length();

        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#0077CC")); // Màu xanh dương
                ds.setUnderlineText(false); // Bỏ gạch chân
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLoginRedirect.setText(ss);
        tvLoginRedirect.setMovementMethod(LinkMovementMethod.getInstance());
        tvLoginRedirect.setHighlightColor(Color.TRANSPARENT); // Bỏ màu nền khi click
    }
}
