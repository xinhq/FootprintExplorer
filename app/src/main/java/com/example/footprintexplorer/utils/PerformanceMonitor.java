package com.example.footprintexplorer.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 性能监控工具类
 * 用于监控应用性能和资源使用情况
 */
public class PerformanceMonitor {
    
    private static final String TAG = "PerformanceMonitor";
    
    /**
     * 检查设备存储空间
     * @param context 上下文
     * @return 可用存储空间（MB）
     */
    public static long checkAvailableStorage(Context context) {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize, availableBlocks;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                availableBlocks = stat.getAvailableBlocks();
            }
            
            return (availableBlocks * blockSize) / (1024 * 1024); // 转换为MB
        } catch (Exception e) {
            Log.e(TAG, "Error checking storage: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 检查应用内存使用情况
     * @param context 上下文
     * @return 应用使用的内存（MB）
     */
    public static double checkAppMemoryUsage(Context context) {
        try {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            return usedMemory / (1024.0 * 1024.0); // 转换为MB
        } catch (Exception e) {
            Log.e(TAG, "Error checking memory usage: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 测量操作执行时间
     * @param operation 要执行的操作
     * @return 执行时间（毫秒）
     */
    public static long measureOperationTime(Runnable operation) {
        long startTime = System.nanoTime();
        operation.run();
        long endTime = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }
    
    /**
     * 检查数据库大小
     * @param context 上下文
     * @return 数据库大小（KB）
     */
    public static long checkDatabaseSize(Context context) {
        try {
            File dbFile = context.getDatabasePath("footprint_database.db");
            if (dbFile.exists()) {
                return dbFile.length() / 1024; // 转换为KB
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking database size: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 检查电池使用情况
     * 注意：需要在清单文件中添加BATTERY_STATS权限
     * @param context 上下文
     * @return 电池电量百分比
     */
    public static int checkBatteryLevel(Context context) {
        try {
            android.os.BatteryManager batteryManager = (android.os.BatteryManager) 
                    context.getSystemService(Context.BATTERY_SERVICE);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && batteryManager != null) {
                return batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }
            
            // 对于较旧的Android版本，可以通过广播获取电池信息
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Error checking battery level: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 记录性能日志
     * @param tag 日志标签
     * @param message 日志消息
     */
    public static void logPerformance(String tag, String message) {
        Log.d(TAG + ":" + tag, message);
    }
}
