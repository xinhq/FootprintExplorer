package com.example.footprintexplorer.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.User;

import java.util.Date;
import java.util.concurrent.Executors;

/**
 * 设置视图模型
 * 用于管理设置界面的数据和状态
 */
public class SettingsViewModel extends AndroidViewModel {

    private FootprintDatabase database;
    private MutableLiveData<String> username = new MutableLiveData<>("");
    private MutableLiveData<String> theme = new MutableLiveData<>("system");
    private MutableLiveData<String> mapType = new MutableLiveData<>("normal");
    private MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> autoTracking = new MutableLiveData<>(false);
    private MutableLiveData<Integer> trackingInterval = new MutableLiveData<>(10);

    public SettingsViewModel(Application application) {
        super(application);
        database = FootprintDatabase.getInstance(application);
        
        // 加载用户设置
        loadUserSettings();
    }
    
    /**
     * 加载用户设置
     */
    private void loadUserSettings() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取用户数据
            User user = database.userDao().getUserByIdSync(1);
            
            // 如果用户不存在，创建默认用户
            if (user == null) {
                user = new User();
                database.userDao().insert(user);
            }
            
            // 更新LiveData
            username.postValue(user.getUsername());
            theme.postValue(user.getPreferredTheme());
            mapType.postValue(user.getPreferredMapType());
            notificationsEnabled.postValue(user.isNotificationsEnabled());
            autoTracking.postValue(user.isAutoTracking());
            trackingInterval.postValue(user.getTrackingInterval());
        });
    }
    
    /**
     * 设置用户名
     */
    public void setUsername(String username) {
        this.username.setValue(username);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = database.userDao().getUserByIdSync(1);
            if (user != null) {
                user.setUsername(username);
                database.userDao().update(user);
            }
        });
    }
    
    /**
     * 设置主题
     */
    public void setTheme(String theme) {
        this.theme.setValue(theme);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            database.userDao().updatePreferredTheme(1, theme);
        });
    }
    
    /**
     * 设置地图类型
     */
    public void setMapType(String mapType) {
        this.mapType.setValue(mapType);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            database.userDao().updatePreferredMapType(1, mapType);
        });
    }
    
    /**
     * 设置通知开关
     */
    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled.setValue(enabled);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            database.userDao().updateNotificationsEnabled(1, enabled);
        });
    }
    
    /**
     * 设置自动追踪开关
     */
    public void setAutoTracking(boolean enabled) {
        this.autoTracking.setValue(enabled);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            database.userDao().updateAutoTracking(1, enabled);
        });
    }
    
    /**
     * 设置追踪间隔
     */
    public void setTrackingInterval(int interval) {
        this.trackingInterval.setValue(interval);
        
        Executors.newSingleThreadExecutor().execute(() -> {
            database.userDao().updateTrackingInterval(1, interval);
        });
    }
    
    /**
     * 清除所有数据
     */
    public void clearAllData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 清除位置记录
            database.locationDao().deleteAllLocations();
            
            // 清除追踪会话
            database.trackingSessionDao().deleteAllSessions();
            
            // 清除地点
            database.placeDao().deleteAllPlaces();
            
            // 清除徽章
            database.badgeDao().deleteAllBadges();
            
            // 清除挑战
            database.challengeDao().deleteAllChallenges();
            
            // 重置用户数据
            User user = database.userDao().getUserByIdSync(1);
            if (user != null) {
                user.setTotalDistance(0);
                user.setTotalPlaces(0);
                user.setTotalBadges(0);
                user.setTotalChallenges(0);
                user.setXp(0);
                user.setLevel(1);
                user.setXpToNextLevel(100);
                user.setLastActiveDate(new Date());
                database.userDao().update(user);
            }
        });
    }
    
    /**
     * 获取用户名
     */
    public LiveData<String> getUsername() {
        return username;
    }
    
    /**
     * 获取主题
     */
    public LiveData<String> getTheme() {
        return theme;
    }
    
    /**
     * 获取地图类型
     */
    public LiveData<String> getMapType() {
        return mapType;
    }
    
    /**
     * 获取通知开关状态
     */
    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    /**
     * 获取自动追踪开关状态
     */
    public LiveData<Boolean> getAutoTracking() {
        return autoTracking;
    }
    
    /**
     * 获取追踪间隔
     */
    public LiveData<Integer> getTrackingInterval() {
        return trackingInterval;
    }
}
