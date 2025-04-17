package com.example.footprintexplorer.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.footprintexplorer.data.entity.User;

/**
 * 用户DAO接口
 * 用于访问用户数据
 */
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> getUserById(long id);

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserByIdSync(long id);

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();

    @Query("UPDATE users SET xp = xp + :amount WHERE id = :id")
    void addXp(long id, int amount);

    @Query("UPDATE users SET totalDistance = totalDistance + :distance WHERE id = :id")
    void addDistance(long id, int distance);

    @Query("UPDATE users SET totalPlaces = totalPlaces + 1 WHERE id = :id")
    void incrementPlaces(long id);

    @Query("UPDATE users SET totalBadges = totalBadges + 1 WHERE id = :id")
    void incrementBadges(long id);

    @Query("UPDATE users SET totalChallenges = totalChallenges + 1 WHERE id = :id")
    void incrementChallenges(long id);

    @Query("UPDATE users SET lastActiveDate = :lastActiveDate WHERE id = :id")
    void updateLastActiveDate(long id, long lastActiveDate);

    @Query("UPDATE users SET preferredMapType = :mapType WHERE id = :id")
    void updatePreferredMapType(long id, String mapType);

    @Query("UPDATE users SET preferredTheme = :theme WHERE id = :id")
    void updatePreferredTheme(long id, String theme);

    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE id = :id")
    void updateNotificationsEnabled(long id, boolean enabled);

    @Query("UPDATE users SET trackingInterval = :interval WHERE id = :id")
    void updateTrackingInterval(long id, int interval);

    @Query("UPDATE users SET autoTracking = :autoTracking WHERE id = :id")
    void updateAutoTracking(long id, boolean autoTracking);
}
