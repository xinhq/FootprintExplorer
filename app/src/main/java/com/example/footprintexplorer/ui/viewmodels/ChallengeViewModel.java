package com.example.footprintexplorer.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.Challenge;
import com.example.footprintexplorer.data.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 挑战任务视图模型
 * 用于管理挑战任务界面的数据和状态
 */
public class ChallengeViewModel extends AndroidViewModel {

    private FootprintDatabase database;
    private MutableLiveData<List<Challenge>> challenges = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Integer> userLevel = new MutableLiveData<>(1);
    private MutableLiveData<Integer> userXp = new MutableLiveData<>(0);
    private MutableLiveData<Integer> userXpProgress = new MutableLiveData<>(0);
    private MutableLiveData<Integer> userXpToNextLevel = new MutableLiveData<>(100);

    public ChallengeViewModel(Application application) {
        super(application);
        database = FootprintDatabase.getInstance(application);
        
        // 加载用户数据
        loadUserData();
        
        // 加载所有挑战任务
        loadAllChallenges();
    }
    
    /**
     * 加载用户数据
     */
    private void loadUserData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取用户数据
            User user = database.userDao().getUserByIdSync(1);
            
            // 如果用户不存在，创建默认用户
            if (user == null) {
                user = new User();
                database.userDao().insert(user);
            }
            
            // 更新LiveData
            userLevel.postValue(user.getLevel());
            userXp.postValue(user.getXp());
            userXpProgress.postValue(user.getXpProgressPercentage());
            userXpToNextLevel.postValue(user.getXpToNextLevel());
        });
    }
    
    /**
     * 加载所有挑战任务
     */
    public void loadAllChallenges() {
        isLoading.setValue(true);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            LiveData<List<Challenge>> challengesLiveData = database.challengeDao().getAllChallenges();
            
            // 观察LiveData变化
            observeChallengesLiveData(challengesLiveData);
        });
    }
    
    /**
     * 加载进行中的挑战任务
     */
    public void loadActiveChallenges() {
        isLoading.setValue(true);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            LiveData<List<Challenge>> challengesLiveData = database.challengeDao().getActiveChallenges();
            
            // 观察LiveData变化
            observeChallengesLiveData(challengesLiveData);
        });
    }
    
    /**
     * 加载已完成的挑战任务
     */
    public void loadCompletedChallenges() {
        isLoading.setValue(true);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            LiveData<List<Challenge>> challengesLiveData = database.challengeDao().getCompletedChallenges();
            
            // 观察LiveData变化
            observeChallengesLiveData(challengesLiveData);
        });
    }
    
    /**
     * 按类型加载挑战任务
     */
    public void loadChallengesByType(String type) {
        isLoading.setValue(true);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            LiveData<List<Challenge>> challengesLiveData = database.challengeDao().getChallengesByType(type);
            
            // 观察LiveData变化
            observeChallengesLiveData(challengesLiveData);
        });
    }
    
    /**
     * 观察挑战任务LiveData变化
     */
    private void observeChallengesLiveData(LiveData<List<Challenge>> challengesLiveData) {
        // 在主线程中观察LiveData
        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        mainHandler.post(() -> {
            challengesLiveData.observeForever(challengeList -> {
                challenges.setValue(challengeList);
                isLoading.setValue(false);
            });
        });
    }
    
    /**
     * 刷新挑战任务
     */
    public void refreshChallenges() {
        // 重新加载当前显示的挑战任务
        loadAllChallenges();
    }
    
    /**
     * 放弃挑战任务
     */
    public void abandonChallenge(Challenge challenge) {
        Executors.newSingleThreadExecutor().execute(() -> {
            database.challengeDao().delete(challenge);
            
            // 刷新挑战任务列表
            refreshChallenges();
        });
    }
    
    /**
     * 获取挑战任务列表
     */
    public LiveData<List<Challenge>> getChallenges() {
        return challenges;
    }
    
    /**
     * 获取加载状态
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * 获取用户等级
     */
    public LiveData<Integer> getUserLevel() {
        return userLevel;
    }
    
    /**
     * 获取用户经验值
     */
    public LiveData<Integer> getUserXp() {
        return userXp;
    }
    
    /**
     * 获取用户经验值进度
     */
    public LiveData<Integer> getUserXpProgress() {
        return userXpProgress;
    }
    
    /**
     * 获取用户下一级所需经验值
     */
    public LiveData<Integer> getUserXpToNextLevel() {
        return userXpToNextLevel;
    }
}
