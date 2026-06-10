package com.example.cashflowscheduler.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cashflowscheduler.data.model.Expense;
import com.example.cashflowscheduler.data.model.Income;
import com.example.cashflowscheduler.data.repository.FinanceRepository;
import com.example.cashflowscheduler.logic.Timeline;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinanceViewModel extends AndroidViewModel {

    private final FinanceRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MutableLiveData<List<Timeline.DayEntry>> timelineResult    = new MutableLiveData<>();
    private final MutableLiveData<String>  overdraftAlert     = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> currentBalanceLive = new MutableLiveData<>(0);

    private String currentUserId;

    public FinanceViewModel(@NonNull Application application) {
        super(application);
        repository = new FinanceRepository(application);
    }

    public void setCurrentUserId(String userId) { this.currentUserId = userId; }
    public String getCurrentUserId() { return currentUserId; }

    public void addIncome(Income i)    { repository.insertIncome(i); }
    public void updateIncome(Income i) { repository.updateIncome(i); }
    public void deleteIncome(Income i) { repository.deleteIncome(i); }
    public LiveData<List<Income>> getAllIncomes() {
        return repository.getAllIncomes(currentUserId);
    }

    public void addExpense(Expense e)    { repository.insertExpense(e); }
    public void updateExpense(Expense e) { repository.updateExpense(e); }
    public void deleteExpense(Expense e) { repository.deleteExpense(e); }
    public LiveData<List<Expense>> getAllExpenses() {
        return repository.getAllExpenses(currentUserId);
    }

    public LiveData<List<Timeline.DayEntry>> getTimelineResult()  { return timelineResult; }
    public LiveData<String>  getOverdraftAlert()                  { return overdraftAlert; }
    public LiveData<Integer> getCurrentBalanceLive()              { return currentBalanceLive; }

    // ── 잔액 조정: 조정 내역을 DB에 추가 ──
    public void adjustBalance(int targetBalance, String reason) {
        executor.execute(() -> {
            String today = LocalDate.now().format(FMT);
            String past  = "2000-01-01";

            int calcBalance = calcBalanceUpTo(today, past);

            int diff = targetBalance - calcBalance;
            if (diff > 0) {
                Income adj = new Income(currentUserId, today,
                        Math.abs(diff), "잔액조정", reason);
                repository.insertIncome(adj);
            } else if (diff < 0) {
                Expense adj = new Expense(currentUserId, today,
                        Math.abs(diff), "잔액조정", reason, false, 0);
                repository.insertExpense(adj);
            }
        });
    }

    // ── 현재 잔액 및 현재 달 타임라인 계산 ──
    public void calculateAll() {
        calculateMonth(YearMonth.now());
    }

    // ── 특정 월 타임라인 계산 ──
    public void calculateMonth(YearMonth yearMonth) {
        executor.execute(() -> {
            String today   = LocalDate.now().format(FMT);
            String past    = "2000-01-01";

            // 현재 잔액 = 오늘까지 모든 내역 합산
            int currentBalance = calcBalanceUpTo(today, past);
            currentBalanceLive.postValue(currentBalance);

            // 해당 월의 시작일 / 종료일
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd   = yearMonth.atEndOfMonth();
            String startStr = monthStart.format(FMT);
            String endStr   = monthEnd.format(FMT);

            // 해당 월 시작 잔액
            // = 해당 월 1일 이전까지의 모든 내역 합산
            String dayBefore = monthStart.minusDays(1).format(FMT);
            int startBalance = calcBalanceUpTo(dayBefore, past);

            // 해당 월 수입/지출
            List<Income>  monthIncomes  = repository.getIncomesByRange(
                    currentUserId, startStr, endStr);
            List<Expense> monthExpenses = repository.getExpensesByRange(
                    currentUserId, startStr, endStr);

            // 고정지출: 현재 달이면 이번 달 것만, 과거 달이면 해당 달 것만
            List<Expense> fixedExpenses = repository.getFixedExpenses(currentUserId);

            // 고정지출 마감 = 해당 월 말일
            Timeline timeline = new Timeline(
                    startBalance, monthIncomes, monthExpenses,
                    fixedExpenses, endStr);
            timeline.buildTimelineForMonth(startStr, endStr);

            List<Timeline.DayEntry> result = timeline.getProjectedTimeline();
            timelineResult.postValue(result);

            // 30일 내 고갈 탐지 (현재 달에서만)
            if (yearMonth.equals(YearMonth.now())) {
                LocalDate ltoday    = LocalDate.now();
                LocalDate deadline = ltoday.plusDays(30);
                String alert = null;

                for (Timeline.DayEntry entry : result) {
                    LocalDate d = LocalDate.parse(entry.date, FMT);

                    // 오늘 이후 날짜에서만 체크
                    if (d.isBefore(ltoday)) continue;

                    if (entry.isDanger && !d.isAfter(deadline)) {
                        alert = d.getMonthValue() + "월 "
                                + d.getDayOfMonth() + "일 잔액 고갈 예상";
                        break;
                    }
                }
                overdraftAlert.postValue(alert);
            } else {
                overdraftAlert.postValue(null);
            }
        });
    }

    // ── 특정 날짜까지의 잔액 계산 (내부 헬퍼) ──
    private int calcBalanceUpTo(String toDate, String fromDate) {
        List<Income>  incomes  = repository.getIncomesByRange(currentUserId, fromDate, toDate);
        List<Expense> expenses = repository.getExpensesByRange(currentUserId, fromDate, toDate);
        List<Expense> fixedList = repository.getFixedExpenses(currentUserId);

        int balance = 0;
        for (Income  i : incomes)  balance += i.getAmount();
        for (Expense e : expenses) balance -= e.getAmount();

        // 고정 지출 중 해당 기간 내 발생한 것
        LocalDate to = LocalDate.parse(toDate, FMT);
        LocalDate from = LocalDate.parse(fromDate, FMT);
        for (Expense f : fixedList) {
            int cycle = f.getCycle() > 0 ? f.getCycle() : 1;
            LocalDate base   = LocalDate.parse(f.getDate(), FMT);
            LocalDate cursor = base;
            while (cursor.isBefore(from)) cursor = cursor.plusMonths(cycle);
            while (!cursor.isAfter(to)) {
                balance -= f.getAmount();
                cursor = cursor.plusMonths(cycle);
            }
        }
        return balance;
    }
}