// ui/fragment/SearchFragment.java
package com.example.cashflowscheduler.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashflowscheduler.R;
import com.example.cashflowscheduler.logic.Timeline;
import com.example.cashflowscheduler.ui.TimelineAdapter;
import com.example.cashflowscheduler.util.CategoryManager;
import com.example.cashflowscheduler.viewmodel.FinanceViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SearchFragment extends Fragment {

    private FinanceViewModel financeViewModel;
    private CategoryManager  categoryManager;
    private TimelineAdapter  adapter;

    // 현재 적용된 필터값 (null = 필터 없음)
    private String filterStartDate = null;
    private String filterEndDate   = null;
    private String filterCategory  = null;
    private String filterMemo      = null;
    private String filterType      = null; // "수입" / "지출" / "고정지출"

    private TextView tvFilterSummary;
    private LinearLayout layoutActiveFilters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        financeViewModel  = new ViewModelProvider(requireActivity()).get(FinanceViewModel.class);
        categoryManager   = new CategoryManager(requireContext());

        tvFilterSummary    = view.findViewById(R.id.tv_filter_summary);
        layoutActiveFilters = view.findViewById(R.id.layout_active_filters);
        TextView tvNoResult = view.findViewById(R.id.tv_no_result);
        Button btnSearch    = view.findViewById(R.id.btn_search);
        Button btnClearAll  = view.findViewById(R.id.btn_clear_all_filters);
        RecyclerView rv     = view.findViewById(R.id.rv_search_result);

        adapter = new TimelineAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        // ── 필터 칩 클릭 ──
        view.findViewById(R.id.chip_date).setOnClickListener(v -> showDateFilterDialog());
        view.findViewById(R.id.chip_category).setOnClickListener(v -> showCategoryFilterDialog());
        view.findViewById(R.id.chip_memo).setOnClickListener(v -> showMemoFilterDialog());
        view.findViewById(R.id.chip_type).setOnClickListener(v -> showTypeFilterDialog());

        // 필터 초기화
        btnClearAll.setOnClickListener(v -> {
            filterStartDate = filterEndDate = filterCategory
                    = filterMemo = filterType = null;
            updateFilterSummary();
            adapter.setItems(new ArrayList<>());
            tvNoResult.setVisibility(View.GONE);
        });

        // 검색
        btnSearch.setOnClickListener(v -> {
            List<Timeline.DayEntry> all =
                    financeViewModel.getTimelineResult().getValue();
            if (all == null) all = new ArrayList<>();

            List<Timeline.DayEntry> filtered = new ArrayList<>();
            for (Timeline.DayEntry entry : all) {
                if (!matchesFilters(entry)) continue;
                filtered.add(entry);
            }
            adapter.setItems(filtered);
            tvNoResult.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    // ── 날짜 범위 필터 팝업 ──
    private void showDateFilterDialog() {
        View dv = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_filter_date, null);

        TextView tvStart = dv.findViewById(R.id.tv_start_date);
        TextView tvEnd   = dv.findViewById(R.id.tv_end_date);

        tvStart.setText(filterStartDate != null ? filterStartDate : "시작일 선택");
        tvEnd.setText(filterEndDate   != null ? filterEndDate   : "종료일 선택");

        dv.findViewById(R.id.btn_pick_start).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                filterStartDate = String.format("%04d-%02d-%02d", y, m + 1, d);
                tvStart.setText(filterStartDate);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        dv.findViewById(R.id.btn_pick_end).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                filterEndDate = String.format("%04d-%02d-%02d", y, m + 1, d);
                tvEnd.setText(filterEndDate);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(requireContext())
                .setTitle("날짜 범위 선택")
                .setView(dv)
                .setPositiveButton("적용", (d, w) -> updateFilterSummary())
                .setNegativeButton("취소", null)
                .show();
    }

    // ── 카테고리 필터 팝업 ──
    private void showCategoryFilterDialog() {
        View dv = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_filter_category, null);

        EditText etInput = dv.findViewById(R.id.et_category_input);
        androidx.appcompat.widget.AppCompatSpinner spinner =
                dv.findViewById(R.id.spinner_category_filter);

        // 수입 + 지출 카테고리 합치기
        List<String> all = new ArrayList<>();
        all.add("전체");
        all.addAll(categoryManager.getIncomeCategories());
        all.addAll(categoryManager.getExpenseCategories());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, all);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (filterCategory != null) etInput.setText(filterCategory);

        new AlertDialog.Builder(requireContext())
                .setTitle("카테고리 선택")
                .setView(dv)
                .setPositiveButton("적용", (d, w) -> {
                    String typed = etInput.getText().toString().trim();
                    if (!typed.isEmpty()) {
                        filterCategory = typed;
                    } else {
                        String selected = spinner.getSelectedItem() != null
                                ? spinner.getSelectedItem().toString() : null;
                        filterCategory = "전체".equals(selected) ? null : selected;
                    }
                    updateFilterSummary();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // ── 상세내역 필터 팝업 ──
    private void showMemoFilterDialog() {
        View dv = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_filter_memo, null);

        EditText etMemo = dv.findViewById(R.id.et_memo_filter);
        if (filterMemo != null) etMemo.setText(filterMemo);

        new AlertDialog.Builder(requireContext())
                .setTitle("상세내역 검색")
                .setView(dv)
                .setPositiveButton("적용", (d, w) -> {
                    String s = etMemo.getText().toString().trim();
                    filterMemo = s.isEmpty() ? null : s;
                    updateFilterSummary();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // ── 내역유형 필터 팝업 ──
    private void showTypeFilterDialog() {
        String[] types = {"전체", "수입", "지출", "고정지출"};
        int currentIdx = 0;
        if (filterType != null) {
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals(filterType)) { currentIdx = i; break; }
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("내역 유형 선택")
                .setSingleChoiceItems(types, currentIdx, (d, which) -> {
                    filterType = which == 0 ? null : types[which];
                    updateFilterSummary();
                    d.dismiss();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // ── 필터 요약 텍스트 갱신 ──
    private void updateFilterSummary() {
        List<String> parts = new ArrayList<>();
        if (filterStartDate != null || filterEndDate != null) {
            parts.add("날짜: " + (filterStartDate != null ? filterStartDate : "~")
                    + " ~ " + (filterEndDate != null ? filterEndDate : "~"));
        }
        if (filterCategory != null) parts.add("카테고리: " + filterCategory);
        if (filterMemo     != null) parts.add("상세내역: " + filterMemo);
        if (filterType     != null) parts.add("유형: " + filterType);

        if (parts.isEmpty()) {
            layoutActiveFilters.setVisibility(View.GONE);
        } else {
            layoutActiveFilters.setVisibility(View.VISIBLE);
            tvFilterSummary.setText(String.join("  |  ", parts));
        }
    }

    // ── 필터 매칭 ──
    private boolean matchesFilters(Timeline.DayEntry entry) {
        if (filterStartDate != null && entry.date.compareTo(filterStartDate) < 0) return false;
        if (filterEndDate   != null && entry.date.compareTo(filterEndDate)   > 0) return false;
        if (filterCategory  != null
                && (entry.category == null || !entry.category.contains(filterCategory)))
            return false;
        if (filterMemo != null
                && (entry.memo == null || !entry.memo.contains(filterMemo)))
            return false;
        if (filterType != null) {
            switch (filterType) {
                case "수입":    if (!entry.isIncome) return false; break;
                case "고정지출": if (!entry.isFixed)  return false; break;
                case "지출":    if (entry.isIncome || entry.isFixed) return false; break;
            }
        }
        return true;
    }
}