package com.example.footprintexplorer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.LocationRecord;
import com.example.footprintexplorer.data.entity.TrackingSession;
import com.example.footprintexplorer.ui.activities.MainActivity;
import com.example.footprintexplorer.utils.BatteryOptimizer;
import com.example.footprintexplorer.utils.LocationUtils;
import com.example.footprintexplorer.utils.PerformanceMonitor;

import java.util.concurrent.Executors;

/**
 * 位置追踪服务
 * 用于在后台持续追踪用户位置
 */
public class LocationTrackingService extends Service implements LocationListener {

    private static final String TAG = "LocationTrackingService";
    private static final String CHANNEL_ID = "location_tracking_channel";
    private static final int NOTIFICATION_ID = 1001;

    private LocationManager locationManager;
    private FootprintDatabase database;
    private long currentSessionId = -1;
    private Location lastLocation = null;
    private long startTime;
    private float totalDistance = 0;
    private boolean isTracking = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "服务创建");
        
        // 初始化位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        // 初始化数据库
        database = FootprintDatabase.getInstance(this);
        
        // 创建通知渠道
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "服务启动");
        
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "START_TRACKING":
                        startTracking();
                        break;
                    case "STOP_TRACKING":
                        stopTracking();
                        break;
                }
            }
        }
        
        // 返回START_STICKY，表示服务被系统杀死后会自动重启
        return START_STICKY;
    }

    /**
     * 开始追踪
     */
    private void startTracking() {
        if (isTracking) {
            Log.d(TAG, "已经在追踪中，忽略请求");
            return;
        }
        
        Log.d(TAG, "开始追踪");
        isTracking = true;
        
        // 创建新的追踪会话
        startTime = System.currentTimeMillis();
        createNewSession();
        
        // 启动前台服务
        startForeground(NOTIFICATION_ID, createNotification("正在追踪您的位置"));
        
        // 请求位置更新
        try {
            // 根据电池状态获取最佳位置更新参数
            long interval = BatteryOptimizer.getOptimalLocationUpdateInterval(this);
            float minDistance = BatteryOptimizer.getOptimalLocationUpdateDistance(this);
            
            Log.d(TAG, "请求位置更新，间隔: " + interval + "ms, 最小距离: " + minDistance + "m");
            
            // 请求位置更新
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    interval,
                    minDistance,
                    this);
            
            // 同时使用网络提供商作为备份
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    interval,
                    minDistance,
                    this);
        } catch (SecurityException e) {
            Log.e(TAG, "没有位置权限: " + e.getMessage());
            stopSelf();
        }
    }

    /**
     * 停止追踪
     */
    private void stopTracking() {
        if (!isTracking) {
            Log.d(TAG, "没有在追踪，忽略请求");
            return;
        }
        
        Log.d(TAG, "停止追踪");
        isTracking = false;
        
        // 停止位置更新
        locationManager.removeUpdates(this);
        
        // 结束当前会话
        endCurrentSession();
        
        // 停止前台服务
        stopForeground(true);
        stopSelf();
    }

    /**
     * 创建新的追踪会话
     */
    private void createNewSession() {
        Executors.newSingleThreadExecutor().execute(() -> {
            TrackingSession session = new TrackingSession();
            session.setStartTime(startTime);
            session.setDistance(0);
            currentSessionId = database.trackingSessionDao().insert(session);
            Log.d(TAG, "创建新会话，ID: " + currentSessionId);
        });
    }

    /**
     * 结束当前会话
     */
    private void endCurrentSession() {
        if (currentSessionId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                TrackingSession session = database.trackingSessionDao().getSessionByIdSync(currentSessionId);
                if (session != null) {
                    session.setEndTime(System.currentTimeMillis());
                    session.setDistance(totalDistance);
                    database.trackingSessionDao().update(session);
                    Log.d(TAG, "结束会话，ID: " + currentSessionId + ", 总距离: " + totalDistance + "m");
                }
            });
        }
    }

    /**
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "位置追踪",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("用于在后台追踪位置");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 创建通知
     */
    private Notification createNotification(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);
        
        // 添加电池状态信息
        String batteryInfo = BatteryOptimizer.getBatteryStateDescription(this);
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("足迹探索")
                .setContentText(text)
                .setSubText(batteryInfo)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * 更新通知
     */
    private void updateNotification(String text) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, createNotification(text));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!isTracking || currentSessionId == -1) {
            return;
        }
        
        Log.d(TAG, "位置更新: " + location.getLatitude() + ", " + location.getLongitude());
        
        // 计算距离
        if (lastLocation != null) {
            float distance = lastLocation.distanceTo(location);
            totalDistance += distance;
            
            // 记录性能数据
            long operationTime = PerformanceMonitor.measureOperationTime(() -> {
                // 检查是否应该记录此位置点（根据电池状态）
                if (BatteryOptimizer.shouldRecordLocation(this, lastLocation, location)) {
                    saveLocationRecord(location);
                } else {
                    Log.d(TAG, "跳过记录位置点（电池优化）");
                }
            });
            
            Log.d(TAG, "位置处理耗时: " + operationTime + "ms");
            
            // 更新通知
            String distanceText = String.format("已行进 %.2f 公里", totalDistance / 1000);
            updateNotification(distanceText);
        } else {
            // 第一个位置点，直接记录
            saveLocationRecord(location);
        }
        
        lastLocation = location;
    }

    /**
     * 保存位置记录
     */
    private void saveLocationRecord(Location location) {
        Executors.newSingleThreadExecutor().execute(() -> {
            LocationRecord record = new LocationRecord();
            record.setSessionId(currentSessionId);
            record.setLatitude(location.getLatitude());
            record.setLongitude(location.getLongitude());
            record.setAltitude(location.getAltitude());
            record.setSpeed(location.getSpeed());
            record.setTimestamp(location.getTime());
            
            long recordId = database.locationDao().insert(record);
            Log.d(TAG, "保存位置记录，ID: " + recordId);
            
            // 检查是否发现新地点
            LocationUtils.checkForNewPlace(this, location, database);
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "位置提供商状态变化: " + provider + ", 状态: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "位置提供商启用: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "位置提供商禁用: " + provider);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "服务销毁");
        
        // 确保停止追踪
        if (isTracking) {
            stopTracking();
        }
        
        super.onDestroy();
    }
}
