package com.example.footprintexplorer.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.footprintexplorer.data.converters.DateConverter;

import java.util.Date;

/**
 * 位置记录实体类
 * 用于存储用户的位置数据点
 */
@Entity(tableName = "location_records")
public class LocationRecord {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long sessionId; // 关联的追踪会话ID
    
    private double latitude; // 纬度
    
    private double longitude; // 经度
    
    private double altitude; // 海拔
    
    private float accuracy; // 精度（米）
    
    private float speed; // 速度（米/秒）
    
    @TypeConverters(DateConverter.class)
    private Date timestamp; // 时间戳
    
    // 构造函数
    public LocationRecord(long sessionId, double latitude, double longitude, 
                         double altitude, float accuracy, float speed, Date timestamp) {
        this.sessionId = sessionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.timestamp = timestamp;
    }
    
    // Getter和Setter方法
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public double getAltitude() {
        return altitude;
    }
    
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    
    public float getAccuracy() {
        return accuracy;
    }
    
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
