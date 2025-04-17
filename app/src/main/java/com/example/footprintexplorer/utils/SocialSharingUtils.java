package com.example.footprintexplorer.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.data.entity.Badge;
import com.example.footprintexplorer.data.entity.Place;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 社交分享工具类
 * 用于分享足迹、徽章和报告到社交媒体
 */
public class SocialSharingUtils {

    /**
     * 分享足迹到社交媒体
     */
    public static void shareFootprint(Context context, Place place, Bitmap mapSnapshot) {
        // 创建分享文本
        String shareText = createFootprintShareText(place);
        
        // 保存地图截图
        Uri imageUri = saveMapSnapshotToCache(context, mapSnapshot);
        
        // 创建分享意图
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        // 启动分享
        context.startActivity(Intent.createChooser(shareIntent, "分享我的足迹"));
    }
    
    /**
     * 分享徽章到社交媒体
     */
    public static void shareBadge(Context context, Badge badge, Bitmap badgeImage) {
        // 创建分享文本
        String shareText = createBadgeShareText(badge);
        
        // 保存徽章图片
        Uri imageUri = saveBadgeImageToCache(context, badgeImage);
        
        // 创建分享意图
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        // 启动分享
        context.startActivity(Intent.createChooser(shareIntent, "分享我的徽章"));
    }
    
    /**
     * 分享统计数据到社交媒体
     */
    public static void shareStats(Context context, float totalDistance, int placesCount, int badgesCount) {
        // 创建分享文本
        String shareText = createStatsShareText(totalDistance, placesCount, badgesCount);
        
        // 创建分享意图
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        
        // 启动分享
        context.startActivity(Intent.createChooser(shareIntent, "分享我的足迹统计"));
    }
    
    /**
     * 分享多个足迹到社交媒体
     */
    public static void shareMultipleFootprints(Context context, List<Place> places, Bitmap mapSnapshot) {
        // 创建分享文本
        String shareText = createMultipleFootprintsShareText(places);
        
        // 保存地图截图
        Uri imageUri = saveMapSnapshotToCache(context, mapSnapshot);
        
        // 创建分享意图
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        // 启动分享
        context.startActivity(Intent.createChooser(shareIntent, "分享我的足迹"));
    }
    
    /**
     * 创建足迹分享文本
     */
    private static String createFootprintShareText(Place place) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("我使用足迹探索App解锁了新地点！\n\n");
        sb.append("📍 ").append(place.getName()).append("\n");
        sb.append("📅 ").append(formatDate(place.getUnlockTime())).append("\n");
        sb.append("🏷️ ").append(place.getCategory()).append("\n\n");
        
        if (place.getDescription() != null && !place.getDescription().isEmpty()) {
            sb.append(place.getDescription()).append("\n\n");
        }
        
        sb.append("来和我一起探索世界吧！#足迹探索 #").append(place.getCategory());
        
        return sb.toString();
    }
    
    /**
     * 创建徽章分享文本
     */
    private static String createBadgeShareText(Badge badge) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("我使用足迹探索App获得了新徽章！\n\n");
        sb.append("🏅 ").append(badge.getName()).append("\n");
        sb.append("📅 ").append(formatDate(badge.getUnlockTime())).append("\n");
        sb.append("🏷️ ").append(badge.getCategory()).append("\n\n");
        
        if (badge.getDescription() != null && !badge.getDescription().isEmpty()) {
            sb.append(badge.getDescription()).append("\n\n");
        }
        
        sb.append("来和我一起收集徽章吧！#足迹探索 #").append(badge.getCategory()).append("徽章");
        
        return sb.toString();
    }
    
    /**
     * 创建统计数据分享文本
     */
    private static String createStatsShareText(float totalDistance, int placesCount, int badgesCount) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("我的足迹探索App统计数据：\n\n");
        sb.append("🚶 总行程：").append(String.format("%.1f 公里", totalDistance / 1000)).append("\n");
        sb.append("🗺️ 解锁地点：").append(placesCount).append(" 个\n");
        sb.append("🏅 获得徽章：").append(badgesCount).append(" 个\n\n");
        
        sb.append("来和我一起探索世界吧！#足迹探索");
        
        return sb.toString();
    }
    
    /**
     * 创建多个足迹分享文本
     */
    private static String createMultipleFootprintsShareText(List<Place> places) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("我使用足迹探索App记录了我的足迹！\n\n");
        
        int count = Math.min(places.size(), 5);
        for (int i = 0; i < count; i++) {
            Place place = places.get(i);
            sb.append("📍 ").append(place.getName());
            if (place.getCategory() != null && !place.getCategory().isEmpty()) {
                sb.append(" (").append(place.getCategory()).append(")");
            }
            sb.append("\n");
        }
        
        if (places.size() > 5) {
            sb.append("... 等 ").append(places.size()).append(" 个地点\n\n");
        } else {
            sb.append("\n");
        }
        
        sb.append("来和我一起探索世界吧！#足迹探索");
        
        return sb.toString();
    }
    
    /**
     * 格式化日期
     */
    private static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }
    
    /**
     * 保存地图截图到缓存
     */
    private static Uri saveMapSnapshotToCache(Context context, Bitmap mapSnapshot) {
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs();
            
            String fileName = "map_snapshot_" + System.currentTimeMillis() + ".png";
            File file = new File(cachePath, fileName);
            
            FileOutputStream stream = new FileOutputStream(file);
            mapSnapshot.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "保存地图截图失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    
    /**
     * 保存徽章图片到缓存
     */
    private static Uri saveBadgeImageToCache(Context context, Bitmap badgeImage) {
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs();
            
            String fileName = "badge_image_" + System.currentTimeMillis() + ".png";
            File file = new File(cachePath, fileName);
            
            FileOutputStream stream = new FileOutputStream(file);
            badgeImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "保存徽章图片失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
