package com.example.footprintexplorer.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footprintexplorer.data.entity.Place;

import java.util.Date;
import java.util.List;

/**
 * 地点DAO接口
 * 用于访问地点数据
 */
@Dao
public interface PlaceDao {
    
    /**
     * 插入地点
     */
    @Insert
    long insert(Place place);
    
    /**
     * 更新地点
     */
    @Update
    void update(Place place);
    
    /**
     * 删除地点
     */
    @Delete
    void delete(Place place);
    
    /**
     * 获取所有地点
     */
    @Query("SELECT * FROM places ORDER BY discoveryDate DESC")
    LiveData<List<Place>> getAllPlaces();
    
    /**
     * 获取指定ID的地点
     */
    @Query("SELECT * FROM places WHERE id = :placeId")
    LiveData<Place> getPlaceById(long placeId);
    
    /**
     * 获取指定城市的所有地点
     */
    @Query("SELECT * FROM places WHERE city = :city ORDER BY discoveryDate DESC")
    LiveData<List<Place>> getPlacesByCity(String city);
    
    /**
     * 获取指定区县的所有地点
     */
    @Query("SELECT * FROM places WHERE district = :district ORDER BY discoveryDate DESC")
    LiveData<List<Place>> getPlacesByDistrict(String district);
    
    /**
     * 获取指定省份的所有地点
     */
    @Query("SELECT * FROM places WHERE province = :province ORDER BY discoveryDate DESC")
    LiveData<List<Place>> getPlacesByProvince(String province);
    
    /**
     * 获取指定时间范围内发现的地点
     */
    @Query("SELECT * FROM places WHERE discoveryDate BETWEEN :startTime AND :endTime ORDER BY discoveryDate DESC")
    LiveData<List<Place>> getPlacesBetween(Date startTime, Date endTime);
    
    /**
     * 检查指定区县是否已解锁
     */
    @Query("SELECT COUNT(*) FROM places WHERE district = :district AND city = :city AND province = :province")
    int isDistrictUnlocked(String district, String city, String province);
    
    /**
     * 获取已解锁地点的数量
     */
    @Query("SELECT COUNT(*) FROM places WHERE isUnlocked = 1")
    int getUnlockedPlacesCount();
}
