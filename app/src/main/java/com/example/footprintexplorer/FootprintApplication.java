package com.example.footprintexplorer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.room.Room;

import com.example.footprintexplorer.data.database.FootprintDatabase;

/**
 * 应用程序类，用于初始化全局组件
 */
public class FootprintApplication extends Application {
    
    private static FootprintApplication instance;
    private FootprintDatabase database;
    
    public static final String LOCATION_CHANNEL_ID = "location_tracking_channel";
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // 初始化数据库
        database = Room.databaseBuilder(getApplicationContext(),
                FootprintDatabase.class, "footprint_database")
                .fallbackToDestructiveMigration()
                .build();
        
        // 创建通知渠道（Android 8.0+）
        createNotificationChannels();
    }
    
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel locationChannel = new NotificationChannel(
                    LOCATION_CHANNEL_ID,
                    "位置追踪",
                    NotificationManager.IMPORTANCE_LOW
            );
            locationChannel.setDescription("用于后台位置追踪的通知");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(locationChannel);
        }
    }
    
    public static FootprintApplication getInstance() {
        return instance;
    }
    
    public FootprintDatabase getDatabase() {
        return database;
    }
}
