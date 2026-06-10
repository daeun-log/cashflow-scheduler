package com.example.cashflowscheduler.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 오늘 날짜 문자열 반환
    public static String today() {
        return LocalDate.now().format(FMT);
    }

    // N개월 후 날짜 반환
    public static String monthsLater(int months) {
        return LocalDate.now().plusMonths(months).format(FMT);
    }

    // 날짜 문자열 유효성 검사
    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, FMT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}