package com.example.footprintexplorer.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.footprintexplorer.data.converters.DateConverter;

import java.util.Date;

/**
 * 地点实体类
 * 用于记录用户解锁的地点
 */
@Entity(tableName = "places")
public class Place {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name; // 地点名称
    
    private String district; // 区县
    
    private String city; // 城市
    
    private String province; // 省份
    
    private double latitude; // 纬度
    
    private double longitude; // 经度
    
    @TypeConverters(DateConverter.class)
    private Date discoveryDate; // 发现日期
    
    private String description; // 地点描述
    
    private String imageUrl; // 地点图片URL
    
    private boolean isUnlocked; // 是否已解锁
    
    // 构造函数
    public Place(String name, String district, String city, String province, 
                double latitude, double longitude, Date discoveryDate) {
        this.name = name;
        this.district = district;
        this.city = city;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
        this.discoveryDate = discoveryDate;
        this.isUnlocked = true;
        this.description = "";
        this.imageUrl = "";
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
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
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
    
    public Date getDiscoveryDate() {
        return discoveryDate;
    }
    
    public void setDiscoveryDate(Date discoveryDate) {
        this.discoveryDate = discoveryDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public boolean isUnlocked() {
        return isUnlocked;
    }
    
    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
    
    // 获取完整地址
    public String getFullAddress() {
        return province + " " + city + " " + district;
    }
}
