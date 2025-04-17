package com.example.footprintexplorer.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.TrackingSession;

import java.util.Date;
import java.util.concurrent.Executors;

/**
 * 主视图模型
 * 用于管理主界面的数据和状态
 */
public class MainViewModel extends AndroidViewModel {

    private FootprintDatabase database;
    private MutableLiveData<Boolean> isTracking = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> permissionsGranted = new MutableLiveData<>(false);
    private MutableLiveData<Long> currentSessionId = new MutableLiveData<>(-1L);

    public MainViewModel(Application application) {
        super(application);
        database = FootprintDatabase.getInstance(application);
    }

    /**
     * 创建新的追踪会话
     */
    public long createNewSession(boolean isManual) {
        TrackingSession session = new TrackingSession("足迹记录 " + new Date(), new Date());
        session.setManualRecording(isManual);
        
        // 在后台线程中插入会话
        Executors.newSingleThreadExecutor().execute(() -> {
            long sessionId = database.trackingSessionDao().insert(session);
            currentSessionId.postValue(sessionId);
        });
        
        return currentSessionId.getValue() != null ? currentSessionId.getValue() : -1;
    }

    /**
     * 获取当前会话ID
     */
    public LiveData<Long> getCurrentSessionId() {
        return currentSessionId;
    }

    /**
     * 设置追踪状态
     */
    public void setTracking(boolean tracking) {
        isTracking.setValue(tracking);
    }

    /**
     * 获取追踪状态
     */
    public LiveData<Boolean> isTracking() {
        return isTracking;
    }

    /**
     * 设置权限状态
     */
    public void setPermissionsGranted(boolean granted) {
        permissionsGranted.setValue(granted);
    }

    /**
     * 获取权限状态
     */
    public LiveData<Boolean> arePermissionsGranted() {
        return permissionsGranted;
    }
}
