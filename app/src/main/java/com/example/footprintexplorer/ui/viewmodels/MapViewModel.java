package com.example.footprintexplorer.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.LocationRecord;
import com.example.footprintexplorer.data.entity.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 地图视图模型
 * 用于管理地图界面的数据和状态
 */
public class MapViewModel extends AndroidViewModel {

    private FootprintDatabase database;
    private MutableLiveData<String> currentFilter = new MutableLiveData<>("all");
    private MutableLiveData<Date> selectedDate = new MutableLiveData<>(new Date());
    private LiveData<List<LocationRecord>> locationRecords;
    private MutableLiveData<List<LatLng>> tracks = new MutableLiveData<>(new ArrayList<>());
    private LiveData<List<Place>> places;

    public MapViewModel(Application application) {
        super(application);
        database = FootprintDatabase.getInstance(application);
        
        // 初始化位置记录数据
        initLocationRecords();
        
        // 初始化地点数据
        initPlaces();
    }

    /**
     * 初始化位置记录数据
     */
    private void initLocationRecords() {
        // 根据过滤器获取位置记录
        locationRecords = Transformations.switchMap(currentFilter, filter -> {
            Date startDate = getStartDateForFilter(filter);
            Date endDate = new Date(); // 当前时间
            
            if (startDate != null) {
                return database.locationDao().getLocationsBetween(startDate, endDate);
            } else {
                // 使用选择的日期
                return Transformations.switchMap(selectedDate, date -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    Date start = calendar.getTime();
                    
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    Date end = calendar.getTime();
                    
                    return database.locationDao().getLocationsBetween(start, end);
                });
            }
        });
        
        // 将位置记录转换为轨迹点
        locationRecords.observeForever(records -> {
            if (records != null) {
                List<LatLng> points = new ArrayList<>();
                for (LocationRecord record : records) {
                    points.add(new LatLng(record.getLatitude(), record.getLongitude()));
                }
                tracks.postValue(points);
            }
        });
    }
    
    /**
     * 初始化地点数据
     */
    private void initPlaces() {
        // 获取所有地点
        places = database.placeDao().getAllPlaces();
    }
    
    /**
     * 根据过滤器获取开始日期
     */
    private Date getStartDateForFilter(String filter) {
        if (filter == null || filter.equals("all")) {
            return null;
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        switch (filter) {
            case "today":
                // 今天开始
                return calendar.getTime();
            case "week":
                // 本周开始
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                return calendar.getTime();
            case "month":
                // 本月开始
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                return calendar.getTime();
            default:
                return null;
        }
    }
    
    /**
     * 设置过滤器
     */
    public void filterTracks(String filter) {
        currentFilter.setValue(filter != null ? filter : "all");
    }
    
    /**
     * 设置选择的日期
     */
    public void setSelectedDate(Date date) {
        selectedDate.setValue(date);
        currentFilter.setValue(null); // 清除预定义过滤器
    }
    
    /**
     * 获取轨迹数据
     */
    public LiveData<List<LatLng>> getTracks() {
        return tracks;
    }
    
    /**
     * 获取地点数据
     */
    public LiveData<List<Place>> getPlaces() {
        return places;
    }
    
    /**
     * 获取当前位置
     */
    public void getCurrentLocation(OnLocationReadyCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            LocationRecord lastLocation = null;
            
            // 获取最新的位置记录
            List<TrackingSession> sessions = database.trackingSessionDao().getAllSessions().getValue();
            if (sessions != null && !sessions.isEmpty()) {
                TrackingSession latestSession = sessions.get(0);
                lastLocation = database.locationDao().getLastLocationBySession(latestSession.getId());
            }
            
            if (lastLocation != null) {
                LatLng location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                callback.onLocationReady(location);
            } else {
                callback.onLocationReady(null);
            }
        });
    }
    
    /**
     * 位置准备回调接口
     */
    public interface OnLocationReadyCallback {
        void onLocationReady(LatLng location);
    }
}
