package com.example.cashflowscheduler.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cashflowscheduler.data.model.User;

@Dao
public interface UserDao {

    // 회원가입: 신규 유저를 DB에 저장
    // IGNORE: 동일한 userId가 이미 있으면 삽입을 무시 (중복 가입 방지)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    // 로그인: ID와 PW가 모두 일치하는 유저를 조회
    // LiveData로 반환 → UI가 자동으로 결과를 관찰(observe)할 수 있음
    @Query("SELECT * FROM users WHERE userId = :userId AND userPw = :userPw LIMIT 1")
    LiveData<User> login(String userId, String userPw);

    // ID 중복 확인: 해당 ID가 이미 존재하는지 확인 (회원가입 시 사용)
    @Query("SELECT COUNT(*) FROM users WHERE userId = :userId")
    LiveData<Integer> checkDuplicateId(String userId);
}