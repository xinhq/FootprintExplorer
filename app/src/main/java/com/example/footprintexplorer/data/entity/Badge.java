package com.example.footprintexplorer.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.footprintexplorer.data.converters.DateConverter;

import java.util.Date;

/**
 * 徽章实体类
 * 用于记录用户解锁的徽章
 */
@Entity(tableName = "badges")
public class Badge {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name; // 徽章名称
    
    private String description; // 徽章描述
    
    private String category; // 徽章类别（美食、文物、动物等）
    
    private String imageUrl; // 徽章图片URL
    
    private long placeId; // 关联的地点ID
    
    @TypeConverters(DateConverter.class)
    private Date unlockDate; // 解锁日期
    
    private boolean isUnlocked; // 是否已解锁
    
    // 构造函数
    public Badge(String name, String description, String category, 
                String imageUrl, long placeId, Date unlockDate) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.placeId = placeId;
        this.unlockDate = unlockDate;
        this.isUnlocked = true;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public long getPlaceId() {
        return placeId;
    }
    
    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }
    
    public Date getUnlockDate() {
        return unlockDate;
    }
    
    public void setUnlockDate(Date unlockDate) {
        this.unlockDate = unlockDate;
    }
    
    public boolean isUnlocked() {
        return isUnlocked;
    }
    
    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}
