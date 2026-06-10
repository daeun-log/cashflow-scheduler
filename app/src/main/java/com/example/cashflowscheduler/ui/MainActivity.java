package com.example.cashflowscheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cashflowscheduler.R;
import com.example.cashflowscheduler.ui.fragment.DashboardFragment;
import com.example.cashflowscheduler.ui.fragment.SearchFragment;
import com.example.cashflowscheduler.viewmodel.AccountViewModel;
import com.example.cashflowscheduler.viewmodel.FinanceViewModel;

public class MainActivity extends AppCompatActivity {

    private AccountViewModel accountViewModel;
    private FinanceViewModel financeViewModel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getStringExtra("userId");
        if (userId == null || userId.isEmpty()) {
            // userId가 없으면 로그인으로 강제 이동
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);
        financeViewModel.setCurrentUserId(userId);

        // 최초 진입 시 대시보드 로드
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
        }

        Button btnDashboard = findViewById(R.id.btn_nav_dashboard);
        Button btnSearch    = findViewById(R.id.btn_nav_search);
        Button btnLogout    = findViewById(R.id.btn_nav_logout);

        btnDashboard.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit());

        btnSearch.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SearchFragment())
                        .commit());

        btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("로그아웃")
                        .setMessage("정말 로그아웃 하시겠습니까?")
                        .setPositiveButton("로그아웃", (dialog, which) -> {
                            accountViewModel.logout();
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("취소", null)
                        .show());
    }
}