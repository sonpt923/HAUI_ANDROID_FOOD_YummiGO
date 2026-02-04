package com.example.btl_android.view.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_android.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button btnContactAdmin;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        // Ánh xạ các nút từ layout
        btnContactAdmin = findViewById(R.id.btnContactAdmin);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        //truyền email
        String email = getIntent().getStringExtra("email");


        // Xử lý sự kiện khi nhấn nút "Liên hệ quản trị viên"
        btnContactAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ví dụ: Mở ứng dụng email để gửi email tới quản trị viên
                String recipient = "admin@yummigo.com"; // Thay bằng email của quản trị viên
                String subject = "Yêu cầu hỗ trợ tài khoản";
                String body = "Xin chào quản trị viên,\n\n"
                        + "Tôi muốn được hỗ trợ về tài khoản của mình.\n\n"
                        + "Trân trọng.";

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút "Đặt lại mật khẩu"
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình nhập email để đặt lại mật khẩu
                // Bạn cần tạo một Activity mới, ví dụ ResetPasswordEmailActivity
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }
}