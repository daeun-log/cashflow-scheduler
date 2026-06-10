package com.example.cashflowscheduler.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cashflowscheduler.R;
import com.example.cashflowscheduler.viewmodel.AccountViewModel;

public class RegisterActivity extends AppCompatActivity {

    private AccountViewModel accountViewModel;
    private EditText etId, etPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        etId = findViewById(R.id.et_reg_id);
        etPw = findViewById(R.id.et_reg_pw);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvGoLogin = findViewById(R.id.tv_go_login);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoLogin.setOnClickListener(v -> finish()); // 뒤로가기로 로그인 화면 복귀
    }

    private void attemptRegister() {
        String id = etId.getText().toString().trim();
        String pw = etPw.getText().toString().trim();

        if (id.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ID 중복 확인
        accountViewModel.checkDuplicateId(id).observe(this, count -> {
            if (count != null && count > 0) {
                Toast.makeText(this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
            } else {
                accountViewModel.join(id, pw);
                Toast.makeText(this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                finish(); // 로그인 화면으로 이동
            }
        });
    }
}