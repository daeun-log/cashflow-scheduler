package com.example.cashflowscheduler.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cashflowscheduler.data.model.User;
import com.example.cashflowscheduler.data.repository.AccountRepository;

public class AccountViewModel extends AndroidViewModel {

    private final AccountRepository repository;

    // 현재 로그인한 유저 (null이면 비로그인 상태)
    private final MutableLiveData<User> currentUser = new MutableLiveData<>(null);

    public AccountViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountRepository(application);
    }

    // 회원가입
    public void join(String userId, String userPw) {
        User newUser = new User(userId, userPw);
        repository.insert(newUser);
    }

    // ID 중복 확인
    public LiveData<Integer> checkDuplicateId(String userId) {
        return repository.checkDuplicateId(userId);
    }

    // 로그인: DB 조회 결과를 LiveData로 반환 → Activity에서 observe
    public LiveData<User> login(String userId, String userPw) {
        return repository.login(userId, userPw);
    }

    // 로그인 성공 시 현재 유저 세션 저장
    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    // 로그아웃: 세션 초기화
    public void logout() {
        currentUser.setValue(null);
    }
}