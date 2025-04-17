package com.example.footprintexplorer.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footprintexplorer.data.entity.Challenge;

import java.util.List;

/**
 * 挑战任务DAO接口
 * 用于访问挑战任务数据
 */
@Dao
public interface ChallengeDao {

    @Insert
    long insert(Challenge challenge);

    @Update
    void update(Challenge challenge);

    @Delete
    void delete(Challenge challenge);

    @Query("SELECT * FROM challenges WHERE id = :id")
    Challenge getChallengeById(long id);

    @Query("SELECT * FROM challenges ORDER BY createTime DESC")
    LiveData<List<Challenge>> getAllChallenges();

    @Query("SELECT * FROM challenges WHERE completed = 0 ORDER BY createTime DESC")
    LiveData<List<Challenge>> getActiveChallenges();

    @Query("SELECT * FROM challenges WHERE completed = 1 ORDER BY completeTime DESC")
    LiveData<List<Challenge>> getCompletedChallenges();

    @Query("SELECT * FROM challenges WHERE type = :type ORDER BY createTime DESC")
    LiveData<List<Challenge>> getChallengesByType(String type);

    @Query("SELECT * FROM challenges WHERE difficulty = :difficulty ORDER BY createTime DESC")
    LiveData<List<Challenge>> getChallengesByDifficulty(int difficulty);

    @Query("SELECT * FROM challenges WHERE placeId = :placeId")
    LiveData<List<Challenge>> getChallengesByPlace(long placeId);

    @Query("SELECT * FROM challenges WHERE badgeCategory = :category")
    LiveData<List<Challenge>> getChallengesByBadgeCategory(String category);

    @Query("SELECT COUNT(*) FROM challenges WHERE completed = 1")
    int getCompletedChallengesCount();

    @Query("SELECT SUM(xpReward) FROM challenges WHERE completed = 1")
    int getTotalXpEarned();

    @Query("UPDATE challenges SET progress = :progress WHERE id = :id")
    void updateProgress(long id, int progress);

    @Query("UPDATE challenges SET completed = 1, completeTime = :completeTime WHERE id = :id")
    void markAsCompleted(long id, long completeTime);
}
