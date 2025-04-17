package com.example.footprintexplorer.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.LocationRecord;
import com.example.footprintexplorer.data.entity.Place;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 报告视图模型
 * 用于管理报告界面的数据和状态
 */
public class ReportViewModel extends AndroidViewModel {

    private FootprintDatabase database;
    private MutableLiveData<Float> totalDistance = new MutableLiveData<>(0f);
    private MutableLiveData<Integer> totalPlaces = new MutableLiveData<>(0);
    private MutableLiveData<Integer> totalBadges = new MutableLiveData<>(0);
    private MutableLiveData<List<Float>> weeklyDistances = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Integer>> placeTypeCount = new MutableLiveData<>(new ArrayList<>());

    public ReportViewModel(Application application) {
        super(application);
        database = FootprintDatabase.getInstance(application);
        
        // 初始化数据
        loadStatistics();
    }

    /**
     * 加载统计数据
     */
    private void loadStatistics() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 计算总距离
            float distance = calculateTotalDistance();
            totalDistance.postValue(distance);
            
            // 获取地点总数
            int places = database.placeDao().getPlaceCount();
            totalPlaces.postValue(places);
            
            // 获取徽章总数
            int badges = database.badgeDao().getUnlockedBadgesCount();
            totalBadges.postValue(badges);
            
            // 获取每周距离
            List<Float> distances = calculateWeeklyDistances();
            weeklyDistances.postValue(distances);
            
            // 获取地点类型统计
            List<Integer> typeCounts = calculatePlaceTypeCounts();
            placeTypeCount.postValue(typeCounts);
        });
    }
    
    /**
     * 计算总距离
     */
    private float calculateTotalDistance() {
        float totalDistance = 0;
        List<LocationRecord> records = database.locationDao().getAllLocations();
        
        if (records.size() < 2) {
            return totalDistance;
        }
        
        for (int i = 1; i < records.size(); i++) {
            LocationRecord prev = records.get(i - 1);
            LocationRecord curr = records.get(i);
            
            // 如果是同一个会话的记录，计算距离
            if (prev.getSessionId() == curr.getSessionId()) {
                float[] results = new float[1];
                android.location.Location.distanceBetween(
                        prev.getLatitude(), prev.getLongitude(),
                        curr.getLatitude(), curr.getLongitude(),
                        results);
                totalDistance += results[0];
            }
        }
        
        return totalDistance;
    }
    
    /**
     * 计算每周距离
     */
    private List<Float> calculateWeeklyDistances() {
        List<Float> distances = new ArrayList<>();
        
        // 初始化7天的距离数据
        for (int i = 0; i < 7; i++) {
            distances.add(0f);
        }
        
        // 获取本周的开始和结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date weekStart = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date weekEnd = calendar.getTime();
        
        // 获取本周的位置记录
        List<LocationRecord> records = database.locationDao().getLocationsBetween(weekStart, weekEnd);
        
        if (records.size() < 2) {
            return distances;
        }
        
        for (int i = 1; i < records.size(); i++) {
            LocationRecord prev = records.get(i - 1);
            LocationRecord curr = records.get(i);
            
            // 如果是同一个会话的记录，计算距离
            if (prev.getSessionId() == curr.getSessionId()) {
                float[] results = new float[1];
                android.location.Location.distanceBetween(
                        prev.getLatitude(), prev.getLongitude(),
                        curr.getLatitude(), curr.getLongitude(),
                        results);
                
                // 确定记录属于周几
                Calendar recordCal = Calendar.getInstance();
                recordCal.setTime(curr.getTimestamp());
                int dayOfWeek = recordCal.get(Calendar.DAY_OF_WEEK) - recordCal.getFirstDayOfWeek();
                if (dayOfWeek < 0) {
                    dayOfWeek += 7;
                }
                
                // 累加距离
                distances.set(dayOfWeek, distances.get(dayOfWeek) + results[0]);
            }
        }
        
        return distances;
    }
    
    /**
     * 计算地点类型统计
     */
    private List<Integer> calculatePlaceTypeCounts() {
        List<Integer> typeCounts = new ArrayList<>();
        
        // 初始化3种类型的计数
        typeCounts.add(0); // 美食
        typeCounts.add(0); // 文物
        typeCounts.add(0); // 动物
        
        // 获取所有地点
        List<Place> places = database.placeDao().getAllPlaces();
        
        for (Place place : places) {
            String category = place.getCategory();
            if (category != null) {
                switch (category) {
                    case "美食":
                        typeCounts.set(0, typeCounts.get(0) + 1);
                        break;
                    case "文物":
                        typeCounts.set(1, typeCounts.get(1) + 1);
                        break;
                    case "动物":
                        typeCounts.set(2, typeCounts.get(2) + 1);
                        break;
                }
            }
        }
        
        return typeCounts;
    }
    
    /**
     * 获取总距离
     */
    public LiveData<Float> getTotalDistance() {
        return totalDistance;
    }
    
    /**
     * 获取地点总数
     */
    public LiveData<Integer> getTotalPlaces() {
        return totalPlaces;
    }
    
    /**
     * 获取徽章总数
     */
    public LiveData<Integer> getTotalBadges() {
        return totalBadges;
    }
    
    /**
     * 获取每周距离
     */
    public LiveData<List<Float>> getWeeklyDistances() {
        return weeklyDistances;
    }
    
    /**
     * 获取地点类型统计
     */
    public LiveData<List<Integer>> getPlaceTypeCount() {
        return placeTypeCount;
    }
    
    /**
     * 生成周报告数据
     */
    public void generateWeeklyReport(OnReportGeneratedListener listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取本周的开始和结束时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date weekStart = calendar.getTime();
            
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date weekEnd = calendar.getTime();
            
            // 生成报告数据
            ReportData reportData = generateReportData(weekStart, weekEnd);
            listener.onReportGenerated(reportData);
        });
    }
    
    /**
     * 生成月报告数据
     */
    public void generateMonthlyReport(OnReportGeneratedListener listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取本月的开始和结束时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date monthStart = calendar.getTime();
            
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date monthEnd = calendar.getTime();
            
            // 生成报告数据
            ReportData reportData = generateReportData(monthStart, monthEnd);
            listener.onReportGenerated(reportData);
        });
    }
    
    /**
     * 生成年报告数据
     */
    public void generateYearlyReport(OnReportGeneratedListener listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 获取本年的开始和结束时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date yearStart = calendar.getTime();
            
            calendar.add(Calendar.YEAR, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date yearEnd = calendar.getTime();
            
            // 生成报告数据
            ReportData reportData = generateReportData(yearStart, yearEnd);
            listener.onReportGenerated(reportData);
        });
    }
    
    /**
     * 生成报告数据
     */
    private ReportData generateReportData(Date startDate, Date endDate) {
        ReportData reportData = new ReportData();
        
        // 获取时间段内的位置记录
        List<LocationRecord> records = database.locationDao().getLocationsBetween(startDate, endDate);
        
        // 计算总距离
        float totalDistance = 0;
        if (records.size() >= 2) {
            for (int i = 1; i < records.size(); i++) {
                LocationRecord prev = records.get(i - 1);
                LocationRecord curr = records.get(i);
                
                // 如果是同一个会话的记录，计算距离
                if (prev.getSessionId() == curr.getSessionId()) {
                    float[] results = new float[1];
                    android.location.Location.distanceBetween(
                            prev.getLatitude(), prev.getLongitude(),
                            curr.getLatitude(), curr.getLongitude(),
                            results);
                    totalDistance += results[0];
                }
            }
        }
        reportData.setTotalDistance(totalDistance);
        
        // 获取时间段内解锁的地点
        List<Place> places = database.placeDao().getPlacesBetween(startDate, endDate);
        reportData.setPlaces(places);
        
        // 获取时间段内解锁的徽章
        List<com.example.footprintexplorer.data.entity.Badge> badges = database.badgeDao().getBadgesBetween(startDate, endDate);
        reportData.setBadges(badges);
        
        return reportData;
    }
    
    /**
     * 报告数据类
     */
    public static class ReportData {
        private float totalDistance;
        private List<Place> places;
        private List<com.example.footprintexplorer.data.entity.Badge> badges;
        
        public float getTotalDistance() {
            return totalDistance;
        }
        
        public void setTotalDistance(float totalDistance) {
            this.totalDistance = totalDistance;
        }
        
        public List<Place> getPlaces() {
            return places;
        }
        
        public void setPlaces(List<Place> places) {
            this.places = places;
        }
        
        public List<com.example.footprintexplorer.data.entity.Badge> getBadges() {
            return badges;
        }
        
        public void setBadges(List<com.example.footprintexplorer.data.entity.Badge> badges) {
            this.badges = badges;
        }
    }
    
    /**
     * 报告生成回调接口
     */
    public interface OnReportGeneratedListener {
        void onReportGenerated(ReportData reportData);
    }
}
