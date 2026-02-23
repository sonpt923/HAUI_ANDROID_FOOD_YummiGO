package com.example.btl_android.view.auth;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.btl_android.Activity.BaseActivity;
import com.example.btl_android.Activity.MainActivity;
import com.example.btl_android.R;
import com.example.btl_android.utils.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvQuenMK, tvLoginRedirect;
    private ImageButton btnShowPassword;
    private TextView tvLangVN, tvLangEN;

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
        tvLangVN = findViewById(R.id.tvLangVN);
        tvLangEN = findViewById(R.id.tvLangEN);

        updateLanguageUI();

        tvLangVN.setOnClickListener(v -> {
            LocaleHelper.setLocale(this, "vi");
            recreate();
        });

        tvLangEN.setOnClickListener(v -> {
            LocaleHelper.setLocale(this, "en");
            recreate();
        });

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
                edtUsername.setError(getString(R.string.enter_email));
                return;
            }

            if (TextUtils.isEmpty(password)) {
                edtPassword.setError(getString(R.string.enter_password));
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                            // Chuyển sang màn hình chính
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_failed) + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
        // Quên mật khẩu
        tvQuenMK.setOnClickListener(view -> {
            String email = edtUsername.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, getString(R.string.enter_email_first), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("email_key", email);
            startActivity(intent);
        });

        setupRegisterRedirect();
    }

    private void updateLanguageUI() {
        String currentLang = LocaleHelper.getLanguage(this);
        if (currentLang.equals("vi")) {
            tvLangVN.setTypeface(null, Typeface.BOLD);
            tvLangVN.setTextColor(Color.BLACK);
            tvLangEN.setTypeface(null, Typeface.NORMAL);
            tvLangEN.setTextColor(Color.GRAY);
        } else {
            tvLangEN.setTypeface(null, Typeface.BOLD);
            tvLangEN.setTextColor(Color.BLACK);
            tvLangVN.setTypeface(null, Typeface.NORMAL);
            tvLangVN.setTextColor(Color.GRAY);
        }
    }

    private void setupRegisterRedirect() {
        String fullText = getString(R.string.register_redirect);
        String registerText = getString(R.string.register_text);
        SpannableString ss = new SpannableString(fullText);
        int start = fullText.indexOf(registerText);
        if (start == -1) return;
        int end = start + registerText.length();

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
