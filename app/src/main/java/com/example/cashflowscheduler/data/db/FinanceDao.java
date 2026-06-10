package com.example.cashflowscheduler.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cashflowscheduler.data.model.Expense;
import com.example.cashflowscheduler.data.model.Income;

import java.util.List;

@Dao
public interface FinanceDao {

    // ────────────── Income (수입) ──────────────

    // 수입 추가
    @Insert
    void insertIncome(Income income);

    // 수입 수정
    @Update
    void updateIncome(Income income);

    // 수입 삭제
    @Delete
    void deleteIncome(Income income);

    // 특정 유저의 전체 수입 목록 조회 (날짜 오름차순 정렬)
    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date ASC")
    LiveData<List<Income>> getAllIncomes(String userId);

    // 수입 검색: 카테고리 또는 출처(source) 키워드로 필터링
    @Query("SELECT * FROM incomes WHERE userId = :userId AND (category LIKE '%' || :keyword || '%' OR source LIKE '%' || :keyword || '%') ORDER BY date ASC")
    LiveData<List<Income>> searchIncomes(String userId, String keyword);

    // 특정 기간의 수입 조회 (타임라인 연산용)
    @Query("SELECT * FROM incomes WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    List<Income> getIncomesByRange(String userId, String startDate, String endDate);


    // ────────────── Expense (지출) ──────────────

    // 지출 추가
    @Insert
    void insertExpense(Expense expense);

    // 지출 수정
    @Update
    void updateExpense(Expense expense);

    // 지출 삭제
    @Delete
    void deleteExpense(Expense expense);

    // 특정 유저의 전체 지출 목록 조회 (날짜 오름차순 정렬)
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date ASC")
    LiveData<List<Expense>> getAllExpenses(String userId);

    // 고정 지출만 조회 (타임라인 반복 연산용)
    @Query("SELECT * FROM expenses WHERE userId = :userId AND isFixed = 1 ORDER BY date ASC")
    List<Expense> getFixedExpenses(String userId);

    // 지출 검색: 카테고리 키워드로 필터링
    @Query("SELECT * FROM expenses WHERE userId = :userId AND category LIKE '%' || :keyword || '%' ORDER BY date ASC")
    LiveData<List<Expense>> searchExpenses(String userId, String keyword);

    // 특정 기간의 지출 조회 (타임라인 연산용)
    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    List<Expense> getExpensesByRange(String userId, String startDate, String endDate);
}