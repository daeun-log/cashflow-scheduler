package com.example.cashflowscheduler.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "incomes")
public class Income {

    @PrimaryKey(autoGenerate = true)
    private int id;           // Room이 자동 생성하는 고유 ID

    private String userId;    // 어떤 유저의 데이터인지 연결하는 외래키 역할
    private String date;      // 수입 날짜 (형식: "YYYY-MM-DD")
    private int amount;       // 수입 금액
    private String category;  // 카테고리 (예: "월급", "용돈", "기타")
    private String source;    // 수입 출처 메모

    // 생성자
    public Income(String userId, String date, int amount, String category, String source) {
        this.userId = userId;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.source = source;
    }

    // Getter / Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}