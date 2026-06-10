package com.example.cashflowscheduler.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashflowscheduler.R;
import com.example.cashflowscheduler.data.model.Expense;
import com.example.cashflowscheduler.data.model.Income;
import com.example.cashflowscheduler.logic.Timeline;
import com.example.cashflowscheduler.ui.TimelineAdapter;
import com.example.cashflowscheduler.util.CategoryManager;
import com.example.cashflowscheduler.util.DateUtils;
import com.example.cashflowscheduler.viewmodel.FinanceViewModel;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FinanceViewModel financeViewModel;
    private TimelineAdapter  adapter;
    private CategoryManager  categoryManager;

    private TextView tvCurrentBalance, tvAlert, tvCurrentMonth;
    private String userId;

    // 현재 보여주는 월
    private YearMonth displayMonth = YearMonth.now();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        financeViewModel = new ViewModelProvider(requireActivity()).get(FinanceViewModel.class);
        userId           = financeViewModel.getCurrentUserId();
        categoryManager  = new CategoryManager(requireContext());

        tvCurrentBalance         = view.findViewById(R.id.tv_current_balance);
        tvAlert                  = view.findViewById(R.id.tv_overdraft_alert);
        tvCurrentMonth           = view.findViewById(R.id.tv_current_month);
        LinearLayout balanceCard = view.findViewById(R.id.layout_balance_card);
        Button btnAddEvent       = view.findViewById(R.id.btn_add_event);
        Button btnPrev           = view.findViewById(R.id.btn_prev_month);
        Button btnNext           = view.findViewById(R.id.btn_next_month);
        RecyclerView rvTimeline  = view.findViewById(R.id.rv_timeline);

        adapter = new TimelineAdapter();
        rvTimeline.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTimeline.setAdapter(adapter);

        adapter.setOnDetailClickListener(this::showDetailDialog);

        // 잔액 카드 클릭
        balanceCard.setOnClickListener(v -> showBalanceEditDialog());

        // 월 네비게이터
        updateMonthLabel();
        btnPrev.setOnClickListener(v -> {
            displayMonth = displayMonth.minusMonths(1);
            updateMonthLabel();
            financeViewModel.calculateMonth(displayMonth);
        });
        btnNext.setOnClickListener(v -> {
            displayMonth = displayMonth.plusMonths(1);
            updateMonthLabel();
            financeViewModel.calculateMonth(displayMonth);
        });

        // DB 변경 감지 → 현재 표시 중인 월 재계산
        financeViewModel.getAllIncomes().observe(getViewLifecycleOwner(),
                i -> financeViewModel.calculateMonth(displayMonth));
        financeViewModel.getAllExpenses().observe(getViewLifecycleOwner(),
                e -> financeViewModel.calculateMonth(displayMonth));

        // 현재 잔액 표시
        financeViewModel.getCurrentBalanceLive().observe(getViewLifecycleOwner(),
                bal -> tvCurrentBalance.setText(
                        String.format(Locale.KOREA, "%,d원", bal)));

        // 타임라인 결과
        financeViewModel.getTimelineResult().observe(getViewLifecycleOwner(),
                entries -> adapter.setItems(entries));

        // 고갈 경고
        financeViewModel.getOverdraftAlert().observe(getViewLifecycleOwner(), alert -> {
            if (alert != null) {
                tvAlert.setText("⚠ " + alert);
                tvAlert.setVisibility(View.VISIBLE);
            } else {
                tvAlert.setVisibility(View.GONE);
            }
        });

        // 내역 추가 버튼
        btnAddEvent.setOnClickListener(v -> navigateToEdit());
    }

    private void updateMonthLabel() {
        tvCurrentMonth.setText(
                displayMonth.getYear() + "년 " + displayMonth.getMonthValue() + "월");
    }

    // ── 상세보기 팝업 ──
    private void showDetailDialog(Timeline.DayEntry entry) {
        View dv = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_entry_detail, null);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dv).create();

        dv.findViewById(R.id.btn_detail_close)
                .setOnClickListener(v -> dialog.dismiss());

        ((TextView) dv.findViewById(R.id.tv_detail_date)).setText(entry.date);
        String typeLabel = entry.isIncome ? "수입" : (entry.isFixed ? "고정지출" : "지출");
        ((TextView) dv.findViewById(R.id.tv_detail_type)).setText(typeLabel);
        ((TextView) dv.findViewById(R.id.tv_detail_category)).setText(entry.category);
        ((TextView) dv.findViewById(R.id.tv_detail_memo))
                .setText(entry.memo.isEmpty() ? "-" : entry.memo);

        TextView tvAmount = dv.findViewById(R.id.tv_detail_amount);
        if (entry.isIncome) {
            tvAmount.setText(String.format(Locale.KOREA, "+%,d원", entry.amount));
            tvAmount.setTextColor(0xFF43A047);
        } else {
            tvAmount.setText(String.format(Locale.KOREA, "-%,d원", entry.amount));
            tvAmount.setTextColor(0xFFE53935);
        }

        // 고정지출 향후 4개월 예정
        LinearLayout layoutFixed = dv.findViewById(R.id.layout_fixed_schedule);
        if (entry.isFixed) {
            layoutFixed.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            java.time.LocalDate base = java.time.LocalDate.parse(entry.date,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            for (int i = 1; i <= 4; i++) {
                java.time.LocalDate next = base.plusMonths(i);
                sb.append(next.getMonthValue()).append("월 ")
                        .append(next.getDayOfMonth()).append("일  ")
                        .append(String.format(Locale.KOREA, "%,d원", entry.amount))
                        .append("\n");
            }
            ((TextView) dv.findViewById(R.id.tv_fixed_schedule))
                    .setText(sb.toString().trim());
        } else {
            layoutFixed.setVisibility(View.GONE);
        }

        dv.findViewById(R.id.btn_detail_edit).setOnClickListener(v -> {
            dialog.dismiss();
            showEditDialog(entry);
        });
        dv.findViewById(R.id.btn_detail_delete).setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirm(entry);
        });

        dialog.show();
    }

    // ── 수정 다이얼로그 ──
    private void showEditDialog(Timeline.DayEntry entry) {
        View dv = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_entry, null);

        EditText etDate   = dv.findViewById(R.id.et_edit_date);
        EditText etAmount = dv.findViewById(R.id.et_edit_amount);
        EditText etMemo   = dv.findViewById(R.id.et_edit_memo);

        etDate.setText(entry.date);
        etAmount.setText(String.valueOf(entry.amount));
        etMemo.setText(entry.memo);

        new AlertDialog.Builder(requireContext())
                .setTitle("내역 수정")
                .setView(dv)
                .setPositiveButton("저장", (d, w) -> {
                    String newDate   = etDate.getText().toString().trim();
                    String amountStr = etAmount.getText().toString().trim();
                    String newMemo   = etMemo.getText().toString().trim();
                    if (!DateUtils.isValidDate(newDate) || amountStr.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "날짜/금액을 올바르게 입력하세요.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int newAmount = Integer.parseInt(amountStr);
                    if (entry.isIncomeType) {
                        Income income = new Income(userId, newDate,
                                newAmount, entry.category, newMemo);
                        income.setId(entry.sourceId);
                        financeViewModel.updateIncome(income);
                    } else {
                        Expense expense = new Expense(userId, newDate,
                                newAmount, entry.category, newMemo,
                                entry.isFixed, entry.isFixed ? 1 : 0);
                        expense.setId(entry.sourceId);
                        financeViewModel.updateExpense(expense);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // ── 삭제 확인 ──
    private void showDeleteConfirm(Timeline.DayEntry entry) {
        new AlertDialog.Builder(requireContext())
                .setTitle("삭제 확인")
                .setMessage(entry.date + "  " + entry.category + "\n"
                        + String.format(Locale.KOREA, "%,d원", entry.amount)
                        + "\n정말 삭제하시겠습니까?")
                .setPositiveButton("삭제", (d, w) -> {
                    if (entry.isIncomeType) {
                        Income income = new Income(userId, entry.date,
                                entry.amount, entry.category, entry.memo);
                        income.setId(entry.sourceId);
                        financeViewModel.deleteIncome(income);
                    } else {
                        Expense expense = new Expense(userId, entry.date,
                                entry.amount, entry.category, entry.memo,
                                entry.isFixed, entry.isFixed ? 1 : 0);
                        expense.setId(entry.sourceId);
                        financeViewModel.deleteExpense(expense);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // ── 잔액 수정 다이얼로그 ──
    private void showBalanceEditDialog() {
        View dv = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_balance_edit, null);

        EditText etBal        = dv.findViewById(R.id.et_new_balance);
        Spinner  spinReason   = dv.findViewById(R.id.spinner_reason);
        Button   btnAddReason = dv.findViewById(R.id.btn_add_reason);
        EditText etNewReason  = dv.findViewById(R.id.et_new_reason);
        Button   btnConfirm   = dv.findViewById(R.id.btn_confirm_reason);

        Integer cur = financeViewModel.getCurrentBalanceLive().getValue();
        etBal.setText(cur != null ? String.valueOf(cur) : "0");
        refreshReasonSpinner(spinReason);

        btnAddReason.setOnClickListener(v -> {
            boolean show = etNewReason.getVisibility() == View.VISIBLE;
            etNewReason.setVisibility(show ? View.GONE : View.VISIBLE);
            btnConfirm.setVisibility(show ? View.GONE : View.VISIBLE);
            if (!show) etNewReason.requestFocus();
        });

        btnConfirm.setOnClickListener(v -> {
            String r = etNewReason.getText().toString().trim();
            if (!r.isEmpty()) {
                categoryManager.addBalanceReason(r);
                refreshReasonSpinner(spinReason);
                ArrayAdapter a = (ArrayAdapter) spinReason.getAdapter();
                int pos = a.getPosition(r);
                if (pos >= 0) spinReason.setSelection(pos);
                etNewReason.setText("");
                etNewReason.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.GONE);
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dv).create();

        dv.findViewById(R.id.btn_dialog_cancel)
                .setOnClickListener(v -> dialog.dismiss());
        dv.findViewById(R.id.btn_dialog_save).setOnClickListener(v -> {
            String balStr = etBal.getText().toString().trim();
            if (balStr.isEmpty()) {
                Toast.makeText(requireContext(),
                        "금액을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            int target = Integer.parseInt(balStr);
            String reason = spinReason.getSelectedItem() != null
                    ? spinReason.getSelectedItem().toString() : "";
            financeViewModel.adjustBalance(target, reason);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void refreshReasonSpinner(Spinner spinner) {
        List<String> reasons = categoryManager.getBalanceReasons();
        ArrayAdapter<String> a = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, reasons);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(a);
    }

    // ── 뒤로가기 후 복귀 시 재계산 ──
    @Override
    public void onResume() {
        super.onResume();
        financeViewModel.calculateMonth(displayMonth);
    }

    private void navigateToEdit() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new EventEditFragment())
                .addToBackStack(null)
                .commit();
    }
}