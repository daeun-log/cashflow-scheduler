package com.example.cashflowscheduler.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cashflowscheduler.R;
import com.example.cashflowscheduler.data.model.Expense;
import com.example.cashflowscheduler.data.model.Income;
import com.example.cashflowscheduler.util.CategoryManager;
import com.example.cashflowscheduler.util.DateUtils;
import com.example.cashflowscheduler.viewmodel.FinanceViewModel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EventEditFragment extends Fragment {

    private FinanceViewModel financeViewModel;
    private CategoryManager categoryManager;

    private EditText etDate, etAmount, etMemo, etNewCategory, etCycle;
    private Spinner spinnerType, spinnerCategory;
    private Button btnPickDate, btnAddCategory, btnConfirmCategory, btnSave, btnBack;

    private final List<String> TYPES = Arrays.asList("수입", "지출 (일회성)", "고정 지출");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        financeViewModel = new ViewModelProvider(requireActivity()).get(FinanceViewModel.class);
        categoryManager  = new CategoryManager(requireContext());

        // ── View 바인딩 ──
        btnBack             = view.findViewById(R.id.btn_back);
        etDate              = view.findViewById(R.id.et_date);
        etAmount            = view.findViewById(R.id.et_amount);
        etMemo              = view.findViewById(R.id.et_memo);
        etNewCategory       = view.findViewById(R.id.et_new_category);
        etCycle             = view.findViewById(R.id.et_cycle);
        spinnerType         = view.findViewById(R.id.spinner_type);
        spinnerCategory     = view.findViewById(R.id.spinner_category);
        btnPickDate         = view.findViewById(R.id.btn_pick_date);
        btnAddCategory      = view.findViewById(R.id.btn_add_category);
        btnConfirmCategory  = view.findViewById(R.id.btn_confirm_category);
        btnSave             = view.findViewById(R.id.btn_save);

        // ── 구분 스피너 설정 ──
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                TYPES);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // 진입 타입에 따라 초기 선택
        String type = getArguments() != null
                ? getArguments().getString("type", "income") : "income";
        if ("expense".equals(type)) spinnerType.setSelection(1);
        else if ("fixed".equals(type)) spinnerType.setSelection(2);

        // 초기 카테고리 스피너
        refreshCategorySpinner(spinnerType.getSelectedItemPosition());

        // ── 구분 변경 → 카테고리 갱신 + 주기 표시 ──
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                refreshCategorySpinner(position);
                etCycle.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ── 달력 버튼 ──
        btnPickDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (dp, year, month, day) -> {
                        etDate.setText(String.format("%04d-%02d-%02d",
                                year, month + 1, day));
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // ── 카테고리 + 추가 버튼 ──
        btnAddCategory.setOnClickListener(v -> {
            boolean showing = etNewCategory.getVisibility() == View.VISIBLE;
            etNewCategory.setVisibility(showing ? View.GONE : View.VISIBLE);
            btnConfirmCategory.setVisibility(showing ? View.GONE : View.VISIBLE);
            if (!showing) etNewCategory.requestFocus();
        });

        btnConfirmCategory.setOnClickListener(v -> {
            String newCat = etNewCategory.getText().toString().trim();
            if (newCat.isEmpty()) {
                Toast.makeText(requireContext(),
                        "카테고리 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            int pos = spinnerType.getSelectedItemPosition();
            if (pos == 0) categoryManager.addIncomeCategory(newCat);
            else          categoryManager.addExpenseCategory(newCat);

            refreshCategorySpinner(pos);

            // 방금 추가한 항목 선택
            ArrayAdapter a = (ArrayAdapter) spinnerCategory.getAdapter();
            int newPos = a.getPosition(newCat);
            if (newPos >= 0) spinnerCategory.setSelection(newPos);

            etNewCategory.setText("");
            etNewCategory.setVisibility(View.GONE);
            btnConfirmCategory.setVisibility(View.GONE);
            Toast.makeText(requireContext(),
                    "카테고리 추가: " + newCat, Toast.LENGTH_SHORT).show();
        });

        // ── 저장 버튼 ──
        btnSave.setOnClickListener(v -> saveEvent());

        // ── 뒤로가기 버튼 ──
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void refreshCategorySpinner(int typePosition) {
        List<String> categories = (typePosition == 0)
                ? categoryManager.getIncomeCategories()
                : categoryManager.getExpenseCategories();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void saveEvent() {
        String date      = etDate.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String memo      = etMemo.getText().toString().trim();
        int typePos      = spinnerType.getSelectedItemPosition();
        String category  = spinnerCategory.getSelectedItem() != null
                ? spinnerCategory.getSelectedItem().toString() : "";

        // ── 유효성 검사 ──
        if (date.isEmpty()) {
            Toast.makeText(requireContext(),
                    "날짜를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!DateUtils.isValidDate(date)) {
            Toast.makeText(requireContext(),
                    "날짜 형식이 올바르지 않습니다. (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(),
                    "금액을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        String userId = financeViewModel.getCurrentUserId();

        if (typePos == 0) {
            // 수입
            Income income = new Income(userId, date, amount, category, memo);
            financeViewModel.addIncome(income);
            Toast.makeText(requireContext(), "수입이 추가되었습니다.", Toast.LENGTH_SHORT).show();

        } else if (typePos == 1) {
            // 일회성 지출
            Expense expense = new Expense(userId, date, amount, category, memo, false, 0);
            financeViewModel.addExpense(expense);
            Toast.makeText(requireContext(), "지출이 추가되었습니다.", Toast.LENGTH_SHORT).show();

        } else {
            // 고정 지출
            String cycleStr = etCycle.getText().toString().trim();
            int cycle = cycleStr.isEmpty() ? 1 : Integer.parseInt(cycleStr);
            Expense fixed = new Expense(userId, date, amount, category, memo, true, cycle);
            financeViewModel.addExpense(fixed);
            Toast.makeText(requireContext(), "고정 지출이 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }

        // 저장 후 대시보드로 복귀
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}