package com.example.footprintexplorer.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * 挑战任务实体类
 * 用于存储游戏化挑战任务
 */
@Entity(tableName = "challenges",
        indices = {@Index("placeId")},
        foreignKeys = @ForeignKey(entity = Place.class,
                parentColumns = "id",
                childColumns = "placeId",
                onDelete = ForeignKey.CASCADE))
public class Challenge {

    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String title;
    private String description;
    private String type; // 挑战类型：距离、地点数量、徽章收集等
    private int target; // 目标值
    private int progress; // 当前进度
    private boolean completed; // 是否完成
    private int xpReward; // 经验值奖励
    private Date createTime; // 创建时间
    private Date completeTime; // 完成时间
    private Long placeId; // 关联的地点ID（可选）
    private String badgeCategory; // 关联的徽章类别（可选）
    private int difficulty; // 难度级别：1-5
    
    public Challenge() {
        this.createTime = new Date();
        this.completed = false;
        this.progress = 0;
    }
    
    public Challenge(String title, String description, String type, int target, int xpReward, int difficulty) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.target = target;
        this.xpReward = xpReward;
        this.difficulty = difficulty;
        this.createTime = new Date();
        this.completed = false;
        this.progress = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        // 检查是否完成挑战
        if (progress >= target && !completed) {
            this.completed = true;
            this.completeTime = new Date();
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && completeTime == null) {
            this.completeTime = new Date();
        }
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getBadgeCategory() {
        return badgeCategory;
    }

    public void setBadgeCategory(String badgeCategory) {
        this.badgeCategory = badgeCategory;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    /**
     * 获取进度百分比
     */
    public int getProgressPercentage() {
        if (target == 0) return 0;
        return (int) ((progress * 100.0f) / target);
    }
}
