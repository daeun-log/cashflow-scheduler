package com.example.cashflowscheduler.util;

import android.content.Context;
import android.content.SharedPreferences;

public class BalanceManager {
    private static final String PREF_NAME = "balance_prefs";
    private static final String KEY_INITIAL = "initial_balance_";
    private static final String KEY_REASON  = "last_reason_";

    private final SharedPreferences prefs;

    public BalanceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 초기(기준) 잔액 저장
    public void saveInitialBalance(String userId, int balance, String reason) {
        prefs.edit()
                .putInt(KEY_INITIAL + userId, balance)
                .putString(KEY_REASON + userId, reason)
                .apply();
    }

    public int getInitialBalance(String userId) {
        return prefs.getInt(KEY_INITIAL + userId, 0);
    }

    public String getLastReason(String userId) {
        return prefs.getString(KEY_REASON + userId, "");
    }
}