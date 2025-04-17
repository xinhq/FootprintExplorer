package com.example.footprintexplorer.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footprintexplorer.data.entity.LocationRecord;

import java.util.Date;
import java.util.List;

/**
 * 位置记录DAO接口
 * 用于访问位置记录数据
 */
@Dao
public interface LocationDao {
    
    /**
     * 插入位置记录
     */
    @Insert
    long insert(LocationRecord locationRecord);
    
    /**
     * 批量插入位置记录
     */
    @Insert
    List<Long> insertAll(List<LocationRecord> locationRecords);
    
    /**
     * 更新位置记录
     */
    @Update
    void update(LocationRecord locationRecord);
    
    /**
     * 删除位置记录
     */
    @Delete
    void delete(LocationRecord locationRecord);
    
    /**
     * 获取指定会话的所有位置记录
     */
    @Query("SELECT * FROM location_records WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    LiveData<List<LocationRecord>> getLocationsBySession(long sessionId);
    
    /**
     * 获取指定时间范围内的所有位置记录
     */
    @Query("SELECT * FROM location_records WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    LiveData<List<LocationRecord>> getLocationsBetween(Date startTime, Date endTime);
    
    /**
     * 获取指定会话的最后一个位置记录
     */
    @Query("SELECT * FROM location_records WHERE sessionId = :sessionId ORDER BY timestamp DESC LIMIT 1")
    LocationRecord getLastLocationBySession(long sessionId);
    
    /**
     * 获取指定会话的位置记录数量
     */
    @Query("SELECT COUNT(*) FROM location_records WHERE sessionId = :sessionId")
    int getLocationCountBySession(long sessionId);
    
    /**
     * 删除指定会话的所有位置记录
     */
    @Query("DELETE FROM location_records WHERE sessionId = :sessionId")
    void deleteLocationsBySession(long sessionId);
}
