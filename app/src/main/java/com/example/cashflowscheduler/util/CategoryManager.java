package com.example.cashflowscheduler.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CategoryManager {

    private static final String PREF_NAME = "categories";
    private static final String KEY_INCOME = "income_categories";
    private static final String KEY_EXPENSE = "expense_categories";

    // 기본 수입 카테고리
    private static final List<String> DEFAULT_INCOME = Arrays.asList(
            "월급", "용돈", "부업", "기타 수입"
    );

    // 기본 지출 카테고리
    private static final List<String> DEFAULT_EXPENSE = Arrays.asList(
            "식비", "교통", "구독", "통신비", "쇼핑", "의료", "여가", "기타 지출"
    );

    private final SharedPreferences prefs;

    public CategoryManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<String> getIncomeCategories() {
        return getCategories(KEY_INCOME, DEFAULT_INCOME);
    }

    public List<String> getExpenseCategories() {
        return getCategories(KEY_EXPENSE, DEFAULT_EXPENSE);
    }

    // 새 카테고리 추가 (중복 방지, 맨 앞에 삽입)
    public void addIncomeCategory(String category) {
        addCategory(KEY_INCOME, DEFAULT_INCOME, category);
    }

    public void addExpenseCategory(String category) {
        addCategory(KEY_EXPENSE, DEFAULT_EXPENSE, category);
    }

    private List<String> getCategories(String key, List<String> defaults) {
        Set<String> saved = prefs.getStringSet(key, null);
        if (saved == null) return new ArrayList<>(defaults);
        // LinkedHashSet으로 순서 유지
        List<String> result = new ArrayList<>(saved);
        // 기본 카테고리 중 없는 것 추가
        for (String d : defaults) {
            if (!result.contains(d)) result.add(d);
        }
        return result;
    }

    private void addCategory(String key, List<String> defaults, String newCategory) {
        List<String> current = getCategories(key, defaults);
        if (!current.contains(newCategory)) {
            current.add(0, newCategory); // 맨 앞에 추가
        }
        // Set으로 저장 (SharedPreferences StringSet 방식)
        prefs.edit().putStringSet(key, new LinkedHashSet<>(current)).apply();
    }
    // CategoryManager.java 안에 추가

    private static final String KEY_BALANCE_REASON = "balance_reasons";

    private static final List<String> DEFAULT_BALANCE_REASONS = Arrays.asList(
            "월급 수령", "용돈 수령", "이체", "현금 충전", "잔액 조정", "기타"
    );

    public List<String> getBalanceReasons() {
        return getCategories(KEY_BALANCE_REASON, DEFAULT_BALANCE_REASONS);
    }

    public void addBalanceReason(String reason) {
        addCategory(KEY_BALANCE_REASON, DEFAULT_BALANCE_REASONS, reason);
    }
}
