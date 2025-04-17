package com.example.footprintexplorer.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.footprintexplorer.data.converters.DateConverter;

import java.util.Date;

/**
 * 追踪会话实体类
 * 用于记录用户的一次完整追踪活动
 */
@Entity(tableName = "tracking_sessions")
public class TrackingSession {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name; // 会话名称
    
    @TypeConverters(DateConverter.class)
    private Date startTime; // 开始时间
    
    @TypeConverters(DateConverter.class)
    private Date endTime; // 结束时间
    
    private double totalDistance; // 总距离（米）
    
    private float averageSpeed; // 平均速度（米/秒）
    
    private int locationCount; // 位置点数量
    
    private boolean isManualRecording; // 是否为手动记录
    
    // 构造函数
    public TrackingSession(String name, Date startTime) {
        this.name = name;
        this.startTime = startTime;
        this.totalDistance = 0;
        this.averageSpeed = 0;
        this.locationCount = 0;
        this.isManualRecording = false;
    }
    
    // Getter和Setter方法
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public double getTotalDistance() {
        return totalDistance;
    }
    
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    public float getAverageSpeed() {
        return averageSpeed;
    }
    
    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
    
    public int getLocationCount() {
        return locationCount;
    }
    
    public void setLocationCount(int locationCount) {
        this.locationCount = locationCount;
    }
    
    public boolean isManualRecording() {
        return isManualRecording;
    }
    
    public void setManualRecording(boolean manualRecording) {
        isManualRecording = manualRecording;
    }
    
    // 计算会话持续时间（毫秒）
    public long getDuration() {
        if (endTime == null) {
            return System.currentTimeMillis() - startTime.getTime();
        }
        return endTime.getTime() - startTime.getTime();
    }
}
