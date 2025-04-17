package com.example.footprintexplorer.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.footprintexplorer.data.converters.DateConverter;
import com.example.footprintexplorer.data.dao.BadgeDao;
import com.example.footprintexplorer.data.dao.LocationDao;
import com.example.footprintexplorer.data.dao.PlaceDao;
import com.example.footprintexplorer.data.dao.TrackingSessionDao;
import com.example.footprintexplorer.data.entity.Badge;
import com.example.footprintexplorer.data.entity.LocationRecord;
import com.example.footprintexplorer.data.entity.Place;
import com.example.footprintexplorer.data.entity.TrackingSession;

/**
 * 应用程序主数据库
 */
@Database(
    entities = {
        LocationRecord.class,
        TrackingSession.class,
        Place.class,
        Badge.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class FootprintDatabase extends RoomDatabase {
    
    /**
     * 获取位置记录DAO
     */
    public abstract LocationDao locationDao();
    
    /**
     * 获取追踪会话DAO
     */
    public abstract TrackingSessionDao trackingSessionDao();
    
    /**
     * 获取地点DAO
     */
    public abstract PlaceDao placeDao();
    
    /**
     * 获取徽章DAO
     */
    public abstract BadgeDao badgeDao();
}
