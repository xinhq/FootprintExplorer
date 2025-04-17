package com.example.footprintexplorer.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footprintexplorer.data.entity.TrackingSession;

import java.util.Date;
import java.util.List;

/**
 * 追踪会话DAO接口
 * 用于访问追踪会话数据
 */
@Dao
public interface TrackingSessionDao {
    
    /**
     * 插入追踪会话
     */
    @Insert
    long insert(TrackingSession trackingSession);
    
    /**
     * 更新追踪会话
     */
    @Update
    void update(TrackingSession trackingSession);
    
    /**
     * 删除追踪会话
     */
    @Delete
    void delete(TrackingSession trackingSession);
    
    /**
     * 获取所有追踪会话
     */
    @Query("SELECT * FROM tracking_sessions ORDER BY startTime DESC")
    LiveData<List<TrackingSession>> getAllSessions();
    
    /**
     * 获取指定ID的追踪会话
     */
    @Query("SELECT * FROM tracking_sessions WHERE id = :sessionId")
    LiveData<TrackingSession> getSessionById(long sessionId);
    
    /**
     * 获取指定时间范围内的追踪会话
     */
    @Query("SELECT * FROM tracking_sessions WHERE startTime BETWEEN :startTime AND :endTime ORDER BY startTime DESC")
    LiveData<List<TrackingSession>> getSessionsBetween(Date startTime, Date endTime);
    
    /**
     * 获取最近的追踪会话
     */
    @Query("SELECT * FROM tracking_sessions ORDER BY startTime DESC LIMIT 1")
    TrackingSession getLatestSession();
    
    /**
     * 获取指定时间范围内的总距离
     */
    @Query("SELECT SUM(totalDistance) FROM tracking_sessions WHERE startTime BETWEEN :startTime AND :endTime")
    double getTotalDistanceBetween(Date startTime, Date endTime);
    
    /**
     * 获取指定时间范围内的平均速度
     */
    @Query("SELECT AVG(averageSpeed) FROM tracking_sessions WHERE startTime BETWEEN :startTime AND :endTime")
    float getAverageSpeedBetween(Date startTime, Date endTime);
    
    /**
     * 获取指定时间范围内的会话数量
     */
    @Query("SELECT COUNT(*) FROM tracking_sessions WHERE startTime BETWEEN :startTime AND :endTime")
    int getSessionCountBetween(Date startTime, Date endTime);
}
