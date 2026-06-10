package com.example.cashflowscheduler.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.cashflowscheduler.data.db.AppDatabase;
import com.example.cashflowscheduler.data.db.FinanceDao;
import com.example.cashflowscheduler.data.model.Expense;
import com.example.cashflowscheduler.data.model.Income;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinanceRepository {

    private final FinanceDao financeDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FinanceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        financeDao = db.financeDao();
    }

    // ── Income ──
    public void insertIncome(Income income) {
        executor.execute(() -> financeDao.insertIncome(income));
    }

    public void updateIncome(Income income) {
        executor.execute(() -> financeDao.updateIncome(income));
    }

    public void deleteIncome(Income income) {
        executor.execute(() -> financeDao.deleteIncome(income));
    }

    public LiveData<List<Income>> getAllIncomes(String userId) {
        return financeDao.getAllIncomes(userId);
    }

    public LiveData<List<Income>> searchIncomes(String userId, String keyword) {
        return financeDao.searchIncomes(userId, keyword);
    }

    // 타임라인 연산용 (백그라운드 스레드에서 호출해야 함)
    public List<Income> getIncomesByRange(String userId, String start, String end) {
        return financeDao.getIncomesByRange(userId, start, end);
    }

    // ── Expense ──
    public void insertExpense(Expense expense) {
        executor.execute(() -> financeDao.insertExpense(expense));
    }

    public void updateExpense(Expense expense) {
        executor.execute(() -> financeDao.updateExpense(expense));
    }

    public void deleteExpense(Expense expense) {
        executor.execute(() -> financeDao.deleteExpense(expense));
    }

    public LiveData<List<Expense>> getAllExpenses(String userId) {
        return financeDao.getAllExpenses(userId);
    }

    public LiveData<List<Expense>> searchExpenses(String userId, String keyword) {
        return financeDao.searchExpenses(userId, keyword);
    }

    public List<Expense> getFixedExpenses(String userId) {
        return financeDao.getFixedExpenses(userId);
    }

    public List<Expense> getExpensesByRange(String userId, String start, String end) {
        return financeDao.getExpensesByRange(userId, start, end);
    }
}