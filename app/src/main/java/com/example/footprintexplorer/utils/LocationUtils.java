package com.example.footprintexplorer.utils;

import android.location.Location;

import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.Badge;
import com.example.footprintexplorer.data.entity.Place;

import java.util.Date;
import java.util.concurrent.Executors;

/**
 * 位置工具类
 * 提供位置相关的辅助方法
 */
public class LocationUtils {

    /**
     * 检查是否发现新地点
     * 
     * @param location 当前位置
     * @param database 数据库实例
     */
    public static void checkNewPlace(Location location, FootprintDatabase database) {
        // 这里应该调用地理编码API获取当前位置的地址信息
        // 为了演示，我们使用模拟数据
        
        // 模拟地址信息
        String district = "模拟区";
        String city = "模拟市";
        String province = "模拟省";
        
        // 检查该区县是否已解锁
        Executors.newSingleThreadExecutor().execute(() -> {
            int count = database.placeDao().isDistrictUnlocked(district, city, province);
            
            if (count == 0) {
                // 未解锁，创建新地点
                Place place = new Place(
                        "新发现的地点",
                        district,
                        city,
                        province,
                        location.getLatitude(),
                        location.getLongitude(),
                        new Date()
                );
                
                // 保存地点
                long placeId = database.placeDao().insert(place);
                
                // 创建徽章
                createBadgesForPlace(placeId, district, city, province, database);
            }
        });
    }
    
    /**
     * 为新地点创建徽章
     */
    private static void createBadgesForPlace(long placeId, String district, String city, 
                                           String province, FootprintDatabase database) {
        // 创建美食徽章
        Badge foodBadge = new Badge(
                district + "美食徽章",
                "发现" + district + "的特色美食",
                "美食",
                "",
                placeId,
                new Date()
        );
        
        // 创建文物徽章
        Badge cultureBadge = new Badge(
                district + "文物徽章",
                "发现" + district + "的历史文化",
                "文物",
                "",
                placeId,
                new Date()
        );
        
        // 创建动物徽章
        Badge animalBadge = new Badge(
                district + "动物徽章",
                "发现" + district + "的特色动物",
                "动物",
                "",
                placeId,
                new Date()
        );
        
        // 保存徽章
        database.badgeDao().insert(foodBadge);
        database.badgeDao().insert(cultureBadge);
        database.badgeDao().insert(animalBadge);
    }
    
    /**
     * 计算两点之间的距离
     */
    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }
}
