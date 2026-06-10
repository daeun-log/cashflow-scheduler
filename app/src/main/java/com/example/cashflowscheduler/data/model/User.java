package com.example.cashflowscheduler.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    private String userId;   // 로그인 ID (기본키)
    private String userPw;   // 비밀번호

    // 생성자
    public User(@NonNull String userId, String userPw) {
        this.userId = userId;
        this.userPw = userPw;
    }

    // Getter / Setter
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    public String getUserPw() { return userPw; }
    public void setUserPw(String userPw) { this.userPw = userPw; }
}