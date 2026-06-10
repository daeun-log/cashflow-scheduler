package com.example.cashflowscheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cashflowscheduler.R;
import com.example.cashflowscheduler.viewmodel.AccountViewModel;

public class LoginActivity extends AppCompatActivity {

    private AccountViewModel accountViewModel;
    private EditText etId, etPw;
    private boolean isObserving = false; // 중복 observe 방지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        etId = findViewById(R.id.et_login_id);
        etPw = findViewById(R.id.et_login_pw);
        Button btnLogin      = findViewById(R.id.btn_login);
        TextView tvGoRegister = findViewById(R.id.tv_go_register);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String id = etId.getText().toString().trim();
        String pw = etPw.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pw.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 중복 observe 방지
        if (isObserving) return;
        isObserving = true;

        // 1단계: 아이디 존재 여부 확인
        accountViewModel.checkDuplicateId(id).observe(this, count -> {
            if (count == null) return;

            if (count == 0) {
                // 존재하지 않는 아이디
                Toast.makeText(this,
                        "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                isObserving = false;
                return;
            }

            // 2단계: 비밀번호 확인
            accountViewModel.login(id, pw).observe(this, user -> {
                if (user == null) {
                    // 비밀번호 불일치
                    Toast.makeText(this,
                            "비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    isObserving = false;
                    return;
                }

                // 로그인 성공
                Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                accountViewModel.setCurrentUser(user);

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userId", user.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });
    }
}