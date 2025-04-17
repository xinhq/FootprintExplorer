package com.example.footprintexplorer.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footprintexplorer.data.entity.Badge;

import java.util.Date;
import java.util.List;

/**
 * 徽章DAO接口
 * 用于访问徽章数据
 */
@Dao
public interface BadgeDao {
    
    /**
     * 插入徽章
     */
    @Insert
    long insert(Badge badge);
    
    /**
     * 更新徽章
     */
    @Update
    void update(Badge badge);
    
    /**
     * 删除徽章
     */
    @Delete
    void delete(Badge badge);
    
    /**
     * 获取所有徽章
     */
    @Query("SELECT * FROM badges ORDER BY unlockDate DESC")
    LiveData<List<Badge>> getAllBadges();
    
    /**
     * 获取指定ID的徽章
     */
    @Query("SELECT * FROM badges WHERE id = :badgeId")
    LiveData<Badge> getBadgeById(long badgeId);
    
    /**
     * 获取指定地点的所有徽章
     */
    @Query("SELECT * FROM badges WHERE placeId = :placeId ORDER BY unlockDate DESC")
    LiveData<List<Badge>> getBadgesByPlace(long placeId);
    
    /**
     * 获取指定类别的所有徽章
     */
    @Query("SELECT * FROM badges WHERE category = :category ORDER BY unlockDate DESC")
    LiveData<List<Badge>> getBadgesByCategory(String category);
    
    /**
     * 获取指定时间范围内解锁的徽章
     */
    @Query("SELECT * FROM badges WHERE unlockDate BETWEEN :startTime AND :endTime ORDER BY unlockDate DESC")
    LiveData<List<Badge>> getBadgesBetween(Date startTime, Date endTime);
    
    /**
     * 获取已解锁徽章的数量
     */
    @Query("SELECT COUNT(*) FROM badges WHERE isUnlocked = 1")
    int getUnlockedBadgesCount();
    
    /**
     * 获取指定类别已解锁徽章的数量
     */
    @Query("SELECT COUNT(*) FROM badges WHERE category = :category AND isUnlocked = 1")
    int getUnlockedBadgesCountByCategory(String category);
}
