package com.example.footprintexplorer.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * 用户实体类
 * 用于存储用户信息和游戏化进度
 */
@Entity(tableName = "users")
public class User {

    @PrimaryKey
    private long id;
    
    private String username;
    private String avatarPath;
    private int level;
    private int xp;
    private int xpToNextLevel;
    private int totalDistance;
    private int totalPlaces;
    private int totalBadges;
    private int totalChallenges;
    private Date joinDate;
    private Date lastActiveDate;
    private String preferredMapType; // 地图类型偏好
    private String preferredTheme; // 主题偏好
    private boolean notificationsEnabled; // 通知开关
    private int trackingInterval; // 追踪间隔（秒）
    private boolean autoTracking; // 自动追踪开关
    
    public User() {
        this.id = 1; // 单用户应用，固定ID为1
        this.level = 1;
        this.xp = 0;
        this.xpToNextLevel = 100;
        this.totalDistance = 0;
        this.totalPlaces = 0;
        this.totalBadges = 0;
        this.totalChallenges = 0;
        this.joinDate = new Date();
        this.lastActiveDate = new Date();
        this.preferredMapType = "normal";
        this.preferredTheme = "system";
        this.notificationsEnabled = true;
        this.trackingInterval = 10;
        this.autoTracking = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
        // 检查是否升级
        while (this.xp >= xpToNextLevel) {
            levelUp();
        }
    }
    
    /**
     * 增加经验值
     */
    public void addXp(int amount) {
        this.xp += amount;
        // 检查是否升级
        while (this.xp >= xpToNextLevel) {
            levelUp();
        }
    }
    
    /**
     * 升级
     */
    private void levelUp() {
        this.xp -= xpToNextLevel;
        this.level++;
        // 每级所需经验值增加
        this.xpToNextLevel = 100 + (level - 1) * 50;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    public void setXpToNextLevel(int xpToNextLevel) {
        this.xpToNextLevel = xpToNextLevel;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    /**
     * 增加总距离
     */
    public void addDistance(int distance) {
        this.totalDistance += distance;
        // 每公里增加5点经验值
        addXp(distance / 200);
    }

    public int getTotalPlaces() {
        return totalPlaces;
    }

    public void setTotalPlaces(int totalPlaces) {
        this.totalPlaces = totalPlaces;
    }
    
    /**
     * 增加地点数量
     */
    public void addPlace() {
        this.totalPlaces++;
        // 每个新地点增加20点经验值
        addXp(20);
    }

    public int getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(int totalBadges) {
        this.totalBadges = totalBadges;
    }
    
    /**
     * 增加徽章数量
     */
    public void addBadge() {
        this.totalBadges++;
        // 每个新徽章增加50点经验值
        addXp(50);
    }

    public int getTotalChallenges() {
        return totalChallenges;
    }

    public void setTotalChallenges(int totalChallenges) {
        this.totalChallenges = totalChallenges;
    }
    
    /**
     * 增加完成的挑战数量
     */
    public void addChallenge(int xpReward) {
        this.totalChallenges++;
        // 增加挑战奖励的经验值
        addXp(xpReward);
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Date getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(Date lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }
    
    /**
     * 更新最后活跃时间
     */
    public void updateLastActiveDate() {
        this.lastActiveDate = new Date();
    }

    public String getPreferredMapType() {
        return preferredMapType;
    }

    public void setPreferredMapType(String preferredMapType) {
        this.preferredMapType = preferredMapType;
    }

    public String getPreferredTheme() {
        return preferredTheme;
    }

    public void setPreferredTheme(String preferredTheme) {
        this.preferredTheme = preferredTheme;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public int getTrackingInterval() {
        return trackingInterval;
    }

    public void setTrackingInterval(int trackingInterval) {
        this.trackingInterval = trackingInterval;
    }

    public boolean isAutoTracking() {
        return autoTracking;
    }

    public void setAutoTracking(boolean autoTracking) {
        this.autoTracking = autoTracking;
    }
    
    /**
     * 获取经验值进度百分比
     */
    public int getXpProgressPercentage() {
        if (xpToNextLevel == 0) return 0;
        return (int) ((xp * 100.0f) / xpToNextLevel);
    }
}
