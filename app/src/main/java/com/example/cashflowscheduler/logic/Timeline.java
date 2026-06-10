package com.example.cashflowscheduler.logic;

import com.example.cashflowscheduler.data.model.Expense;
import com.example.cashflowscheduler.data.model.Income;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Timeline {

    private final int currentBalance;
    private final List<Income>  incomes;
    private final List<Expense> oneTimeExpenses;
    private final List<Expense> fixedExpenses;
    private final String fixedEndDate;
    private final List<DayEntry> projectedTimeline = new ArrayList<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Timeline(int currentBalance, List<Income> incomes,
                    List<Expense> oneTimeExpenses, List<Expense> fixedExpenses,
                    String fixedEndDate) {
        this.currentBalance  = currentBalance;
        this.incomes         = incomes;
        this.oneTimeExpenses = oneTimeExpenses;
        this.fixedExpenses   = fixedExpenses;
        this.fixedEndDate    = fixedEndDate;
    }

    /**
     * 월별 타임라인 빌드
     * - 수입/지출: 해당 월 내 것만
     * - 고정지출: 해당 월 내 발생하는 것만 (fixedEndDate = 해당 월 말일)
     */
    public void buildTimelineForMonth(String startDate, String endDate) {
        projectedTimeline.clear();

        LocalDate start    = LocalDate.parse(startDate, FMT);
        LocalDate end      = LocalDate.parse(endDate,   FMT);
        LocalDate fixedEnd = LocalDate.parse(fixedEndDate, FMT);

        List<DayEntry> allEvents = new ArrayList<>();

        // 수입
        for (Income income : incomes) {
            LocalDate d = LocalDate.parse(income.getDate(), FMT);
            if (!d.isBefore(start) && !d.isAfter(end)) {
                allEvents.add(new DayEntry(
                        income.getDate(), income.getCategory(),
                        income.getSource(), income.getAmount(),
                        true, false, income.getId(), true));
            }
        }

        // 일회성 지출
        for (Expense exp : oneTimeExpenses) {
            if (exp.isFixed()) continue;
            LocalDate d = LocalDate.parse(exp.getDate(), FMT);
            if (!d.isBefore(start) && !d.isAfter(end)) {
                allEvents.add(new DayEntry(
                        exp.getDate(), exp.getCategory(),
                        exp.getMemo(),   // ← "" 대신 getMemo()
                        exp.getAmount(),
                        false, false, exp.getId(), false));
            }
        }

        // 고정 지출: 해당 월(start ~ fixedEnd) 내 발생하는 것만
        for (Expense fixed : fixedExpenses) {
            int cycle = fixed.getCycle() > 0 ? fixed.getCycle() : 1;
            LocalDate base   = LocalDate.parse(fixed.getDate(), FMT);
            LocalDate cursor = base;
            // 해당 월 시작 이전이면 앞당김
            while (cursor.isBefore(start)) cursor = cursor.plusMonths(cycle);
            // 해당 월 내 발생하는 것만
            while (!cursor.isAfter(fixedEnd)) {
                allEvents.add(new DayEntry(
                        cursor.format(FMT), fixed.getCategory(),
                        fixed.getMemo(),   // ← "" 대신 getMemo()
                        fixed.getAmount(),
                        false, true, fixed.getId(), false));
                cursor = cursor.plusMonths(cycle);
            }
        }

        // 날짜 오름차순 정렬
        Collections.sort(allEvents, (a, b) -> a.date.compareTo(b.date));

        // 해당 월 시작 잔액 기준으로 누적 계산
        int balance = currentBalance;
        for (DayEntry entry : allEvents) {
            balance += entry.isIncome ? entry.amount : -entry.amount;
            entry.balanceAfter = balance;
            entry.isDanger     = balance < 0;
            projectedTimeline.add(entry);
        }
    }

    public List<DayEntry> getProjectedTimeline() { return projectedTimeline; }

    public static class DayEntry {
        public String  date, category, memo;
        public int     amount, balanceAfter, sourceId;
        public boolean isIncome, isFixed, isIncomeType, isDanger;

        public DayEntry(String date, String category, String memo,
                        int amount, boolean isIncome, boolean isFixed,
                        int sourceId, boolean isIncomeType) {
            this.date         = date;
            this.category     = category;
            this.memo         = memo != null ? memo : "";
            this.amount       = amount;
            this.isIncome     = isIncome;
            this.isFixed      = isFixed;
            this.sourceId     = sourceId;
            this.isIncomeType = isIncomeType;
        }
    }
}