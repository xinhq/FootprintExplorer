package com.example.footprintexplorer.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.footprintexplorer.data.database.FootprintDatabase;
import com.example.footprintexplorer.data.entity.Badge;

import java.util.List;

/**
 * 徽章视图模型
 * 用于管理徽章界面的数据和状态
 */
public class BadgeViewModel extends AndroidViewModel {

    private FootprintDatabase database;
    private MutableLiveData<String> currentFilter = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private LiveData<List<Badge>> badges;

    public BadgeViewModel(Application application) {
        super(application);
        database = FootprintDatabase.getInstance(application);
        
        // 初始化徽章数据
        initBadges();
    }

    /**
     * 初始化徽章数据
     */
    private void initBadges() {
        // 根据过滤器获取徽章
        badges = Transformations.switchMap(currentFilter, filter -> {
            isLoading.setValue(true);
            
            LiveData<List<Badge>> result;
            if (filter == null || filter.isEmpty()) {
                // 获取所有徽章
                result = database.badgeDao().getAllBadges();
            } else {
                // 获取指定类别的徽章
                result = database.badgeDao().getBadgesByCategory(filter);
            }
            
            isLoading.setValue(false);
            return result;
        });
    }
    
    /**
     * 设置过滤器
     */
    public void filterBadges(String category) {
        currentFilter.setValue(category);
    }
    
    /**
     * 获取徽章数据
     */
    public LiveData<List<Badge>> getBadges() {
        return badges;
    }
    
    /**
     * 获取加载状态
     */
    public LiveData<Boolean> isLoading() {
        return isLoading;
    }
    
    /**
     * 获取已解锁徽章数量
     */
    public int getUnlockedBadgesCount() {
        return database.badgeDao().getUnlockedBadgesCount();
    }
    
    /**
     * 获取指定类别已解锁徽章数量
     */
    public int getUnlockedBadgesCountByCategory(String category) {
        return database.badgeDao().getUnlockedBadgesCountByCategory(category);
    }
}
