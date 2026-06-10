package com.example.cashflowscheduler.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;
    private String date;
    private int    amount;
    private String category;
    private String memo;      // ← 추가
    private boolean isFixed;
    private int    cycle;

    public Expense(String userId, String date, int amount,
                   String category, String memo,
                   boolean isFixed, int cycle) {
        this.userId   = userId;
        this.date     = date;
        this.amount   = amount;
        this.category = category;
        this.memo     = memo != null ? memo : "";
        this.isFixed  = isFixed;
        this.cycle    = cycle;
    }

    // Getter / Setter
    public int     getId()       { return id; }
    public void    setId(int id) { this.id = id; }

    public String  getUserId()              { return userId; }
    public void    setUserId(String v)      { userId = v; }

    public String  getDate()                { return date; }
    public void    setDate(String v)        { date = v; }

    public int     getAmount()              { return amount; }
    public void    setAmount(int v)         { amount = v; }

    public String  getCategory()            { return category; }
    public void    setCategory(String v)    { category = v; }

    public String  getMemo()                { return memo; }
    public void    setMemo(String v)        { memo = v != null ? v : ""; }

    public boolean isFixed()               { return isFixed; }
    public void    setFixed(boolean v)     { isFixed = v; }

    public int     getCycle()              { return cycle; }
    public void    setCycle(int v)         { cycle = v; }
}