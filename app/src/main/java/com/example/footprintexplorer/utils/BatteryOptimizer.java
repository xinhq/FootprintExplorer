package com.example.footprintexplorer.utils;

import android.content.Context;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

/**
 * 电池优化工具类
 * 用于优化位置追踪过程中的电池使用
 */
public class BatteryOptimizer {
    
    private static final String TAG = "BatteryOptimizer";
    
    // 电池电量阈值，低于此值时启用省电模式
    private static final int LOW_BATTERY_THRESHOLD = 20;
    
    // 不同电量级别下的位置更新间隔（毫秒）
    private static final long INTERVAL_HIGH_BATTERY = 5000; // 5秒
    private static final long INTERVAL_MEDIUM_BATTERY = 15000; // 15秒
    private static final long INTERVAL_LOW_BATTERY = 30000; // 30秒
    
    // 不同电量级别下的位置更新距离（米）
    private static final float DISTANCE_HIGH_BATTERY = 5; // 5米
    private static final float DISTANCE_MEDIUM_BATTERY = 10; // 10米
    private static final float DISTANCE_LOW_BATTERY = 20; // 20米
    
    // 电池状态
    public enum BatteryState {
        HIGH,    // 高电量 (>70%)
        MEDIUM,  // 中等电量 (20-70%)
        LOW      // 低电量 (<20%)
    }
    
    /**
     * 获取当前电池状态
     * @param context 上下文
     * @return 电池状态
     */
    public static BatteryState getBatteryState(Context context) {
        int batteryLevel = getBatteryLevel(context);
        
        if (batteryLevel >= 70) {
            return BatteryState.HIGH;
        } else if (batteryLevel >= LOW_BATTERY_THRESHOLD) {
            return BatteryState.MEDIUM;
        } else {
            return BatteryState.LOW;
        }
    }
    
    /**
     * 获取电池电量
     * @param context 上下文
     * @return 电池电量百分比
     */
    public static int getBatteryLevel(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
                if (batteryManager != null) {
                    return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                }
            }
            
            // 对于较旧的Android版本，可以通过广播获取电池信息
            // 这里简化处理，返回默认值
            return 50;
        } catch (Exception e) {
            Log.e(TAG, "Error getting battery level: " + e.getMessage());
            return 50; // 默认返回中等电量
        }
    }
    
    /**
     * 根据电池状态获取最佳位置更新间隔
     * @param context 上下文
     * @return 位置更新间隔（毫秒）
     */
    public static long getOptimalLocationUpdateInterval(Context context) {
        BatteryState state = getBatteryState(context);
        
        switch (state) {
            case HIGH:
                return INTERVAL_HIGH_BATTERY;
            case MEDIUM:
                return INTERVAL_MEDIUM_BATTERY;
            case LOW:
                return INTERVAL_LOW_BATTERY;
            default:
                return INTERVAL_MEDIUM_BATTERY;
        }
    }
    
    /**
     * 根据电池状态获取最佳位置更新距离
     * @param context 上下文
     * @return 位置更新距离（米）
     */
    public static float getOptimalLocationUpdateDistance(Context context) {
        BatteryState state = getBatteryState(context);
        
        switch (state) {
            case HIGH:
                return DISTANCE_HIGH_BATTERY;
            case MEDIUM:
                return DISTANCE_MEDIUM_BATTERY;
            case LOW:
                return DISTANCE_LOW_BATTERY;
            default:
                return DISTANCE_MEDIUM_BATTERY;
        }
    }
    
    /**
     * 判断是否应该记录位置点
     * 根据电池状态和与上一个点的距离决定是否记录当前位置
     * @param context 上下文
     * @param lastLocation 上一个记录的位置
     * @param currentLocation 当前位置
     * @return 是否应该记录
     */
    public static boolean shouldRecordLocation(Context context, Location lastLocation, Location currentLocation) {
        // 如果没有上一个位置，则记录当前位置
        if (lastLocation == null) {
            return true;
        }
        
        // 获取与上一个位置的距离
        float distance = lastLocation.distanceTo(currentLocation);
        
        // 根据电池状态获取最小距离阈值
        float minDistance = getOptimalLocationUpdateDistance(context);
        
        // 如果距离大于阈值，则记录当前位置
        return distance >= minDistance;
    }
    
    /**
     * 是否处于省电模式
     * @param context 上下文
     * @return 是否处于省电模式
     */
    public static boolean isInPowerSavingMode(Context context) {
        return getBatteryState(context) == BatteryState.LOW;
    }
    
    /**
     * 获取当前电池状态的描述
     * @param context 上下文
     * @return 电池状态描述
     */
    public static String getBatteryStateDescription(Context context) {
        BatteryState state = getBatteryState(context);
        int level = getBatteryLevel(context);
        
        switch (state) {
            case HIGH:
                return "电池电量充足 (" + level + "%)，正常追踪";
            case MEDIUM:
                return "电池电量适中 (" + level + "%)，适度追踪";
            case LOW:
                return "电池电量低 (" + level + "%)，省电模式";
            default:
                return "电池状态未知";
        }
    }
}
