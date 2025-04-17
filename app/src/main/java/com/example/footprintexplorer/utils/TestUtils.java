package com.example.footprintexplorer.utils;

import android.content.Context;
import android.util.Log;

import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.LocationRecord;
import com.example.footprintexplorer.data.entity.Place;
import com.example.footprintexplorer.data.entity.TrackingSession;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * 测试工具类
 * 用于测试应用的各项功能
 */
public class TestUtils {
    
    private static final String TAG = "TestUtils";
    
    /**
     * 测试数据库操作性能
     * @param context 上下文
     * @return 测试结果
     */
    public static String testDatabasePerformance(Context context) {
        StringBuilder result = new StringBuilder();
        FootprintDatabase database = FootprintDatabase.getInstance(context);
        
        // 测试插入性能
        long insertTime = PerformanceMonitor.measureOperationTime(() -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                // 插入测试会话
                TrackingSession session = new TrackingSession();
                session.setStartTime(System.currentTimeMillis());
                session.setEndTime(System.currentTimeMillis() + 3600000); // 1小时后
                session.setDistance(1000); // 1公里
                long sessionId = database.trackingSessionDao().insert(session);
                
                // 插入测试位置记录
                for (int i = 0; i < 100; i++) {
                    LocationRecord record = new LocationRecord();
                    record.setSessionId(sessionId);
                    record.setLatitude(39.9 + (i * 0.001));
                    record.setLongitude(116.3 + (i * 0.001));
                    record.setAltitude(50 + i);
                    record.setSpeed(5);
                    record.setTimestamp(System.currentTimeMillis() + (i * 60000)); // 每分钟一个点
                    database.locationDao().insert(record);
                }
            });
        });
        
        result.append("插入100条位置记录耗时: ").append(insertTime).append("ms\n");
        
        // 测试查询性能
        long queryTime = PerformanceMonitor.measureOperationTime(() -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                List<TrackingSession> sessions = database.trackingSessionDao().getAllSessionsSync();
                if (!sessions.isEmpty()) {
                    long sessionId = sessions.get(0).getId();
                    List<LocationRecord> records = database.locationDao().getLocationsBySessionIdSync(sessionId);
                    Log.d(TAG, "查询到 " + records.size() + " 条位置记录");
                }
            });
        });
        
        result.append("查询位置记录耗时: ").append(queryTime).append("ms\n");
        
        // 测试数据库大小
        long dbSize = PerformanceMonitor.checkDatabaseSize(context);
        result.append("数据库大小: ").append(dbSize).append("KB\n");
        
        return result.toString();
    }
    
    /**
     * 测试位置计算性能
     * @param context 上下文
     * @return 测试结果
     */
    public static String testLocationCalculations(Context context) {
        StringBuilder result = new StringBuilder();
        
        // 创建测试位置数据
        android.location.Location loc1 = new android.location.Location("");
        loc1.setLatitude(39.9);
        loc1.setLongitude(116.3);
        
        android.location.Location loc2 = new android.location.Location("");
        loc2.setLatitude(40.0);
        loc2.setLongitude(116.4);
        
        // 测试距离计算性能
        long distanceCalcTime = PerformanceMonitor.measureOperationTime(() -> {
            for (int i = 0; i < 1000; i++) {
                float distance = loc1.distanceTo(loc2);
            }
        });
        
        result.append("1000次距离计算耗时: ").append(distanceCalcTime).append("ms\n");
        
        // 测试地点识别性能
        long placeDetectionTime = PerformanceMonitor.measureOperationTime(() -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                // 模拟地点识别逻辑
                Place place = new Place();
                place.setName("测试地点");
                place.setLatitude(39.9);
                place.setLongitude(116.3);
                place.setType("测试");
                place.setDiscoveryTime(System.currentTimeMillis());
                database.placeDao().insert(place);
            });
        });
        
        result.append("地点识别处理耗时: ").append(placeDetectionTime).append("ms\n");
        
        return result.toString();
    }
    
    /**
     * 测试UI渲染性能
     * @param context 上下文
     * @return 测试结果
     */
    public static String testUIPerformance(Context context) {
        StringBuilder result = new StringBuilder();
        
        // 测试内存使用情况
        double memoryUsage = PerformanceMonitor.checkAppMemoryUsage(context);
        result.append("应用内存使用: ").append(String.format("%.2f", memoryUsage)).append("MB\n");
        
        // 测试存储空间
        long availableStorage = PerformanceMonitor.checkAvailableStorage(context);
        result.append("可用存储空间: ").append(availableStorage).append("MB\n");
        
        return result.toString();
    }
    
    /**
     * 测试电池优化
     * @param context 上下文
     * @return 测试结果
     */
    public static String testBatteryOptimization(Context context) {
        StringBuilder result = new StringBuilder();
        
        // 获取当前电池状态
        BatteryOptimizer.BatteryState state = BatteryOptimizer.getBatteryState(context);
        int batteryLevel = BatteryOptimizer.getBatteryLevel(context);
        
        result.append("当前电池电量: ").append(batteryLevel).append("%\n");
        result.append("电池状态: ").append(state).append("\n");
        
        // 获取优化后的位置更新参数
        long interval = BatteryOptimizer.getOptimalLocationUpdateInterval(context);
        float distance = BatteryOptimizer.getOptimalLocationUpdateDistance(context);
        
        result.append("优化后的位置更新间隔: ").append(interval).append("ms\n");
        result.append("优化后的位置更新距离: ").append(distance).append("m\n");
        
        // 测试省电模式
        boolean powerSavingMode = BatteryOptimizer.isInPowerSavingMode(context);
        result.append("是否处于省电模式: ").append(powerSavingMode).append("\n");
        
        return result.toString();
    }
    
    /**
     * 运行所有测试
     * @param context 上下文
     * @return 测试结果
     */
    public static String runAllTests(Context context) {
        StringBuilder result = new StringBuilder();
        result.append("===== 足迹探索应用测试报告 =====\n\n");
        
        result.append("--- 数据库性能测试 ---\n");
        result.append(testDatabasePerformance(context)).append("\n");
        
        result.append("--- 位置计算性能测试 ---\n");
        result.append(testLocationCalculations(context)).append("\n");
        
        result.append("--- UI性能测试 ---\n");
        result.append(testUIPerformance(context)).append("\n");
        
        result.append("--- 电池优化测试 ---\n");
        result.append(testBatteryOptimization(context)).append("\n");
        
        result.append("===== 测试完成 =====");
        
        return result.toString();
    }
}
