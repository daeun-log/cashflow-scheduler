package com.example.cashflowscheduler.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.cashflowscheduler.data.db.AppDatabase;
import com.example.cashflowscheduler.data.db.UserDao;
import com.example.cashflowscheduler.data.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountRepository {

    private final UserDao userDao;
    // 백그라운드 스레드 실행기 (Room은 메인 스레드에서 쓰기 불가)
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }

    // 회원가입: 백그라운드에서 insert 실행
    public void insert(User user) {
        executor.execute(() -> userDao.insert(user));
    }

    // 로그인 인증: LiveData 반환 (ViewModel이 observe)
    public LiveData<User> login(String userId, String userPw) {
        return userDao.login(userId, userPw);
    }

    // ID 중복 확인
    public LiveData<Integer> checkDuplicateId(String userId) {
        return userDao.checkDuplicateId(userId);
    }
}