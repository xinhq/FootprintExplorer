package com.example.footprintexplorer.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.databinding.ActivityMainBinding;
import com.example.footprintexplorer.ui.fragments.BadgesFragment;
import com.example.footprintexplorer.ui.fragments.ChallengesFragment;
import com.example.footprintexplorer.ui.fragments.HomeFragment;
import com.example.footprintexplorer.ui.fragments.MapFragment;
import com.example.footprintexplorer.ui.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主活动
 * 应用的主界面，包含底部导航栏和各个功能页面
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用视图绑定
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        
        // 设置底部导航
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this);
        
        // 默认显示首页
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    /**
     * 底部导航项选择事件
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        
        // 根据选择的菜单项加载对应的Fragment
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            fragment = new HomeFragment();
            binding.toolbar.setTitle("足迹探索");
        } else if (itemId == R.id.navigation_map) {
            fragment = new MapFragment();
            binding.toolbar.setTitle("我的足迹");
        } else if (itemId == R.id.navigation_badges) {
            fragment = new BadgesFragment();
            binding.toolbar.setTitle("我的徽章");
        } else if (itemId == R.id.navigation_challenges) {
            fragment = new ChallengesFragment();
            binding.toolbar.setTitle("挑战任务");
        } else if (itemId == R.id.navigation_settings) {
            fragment = new SettingsFragment();
            binding.toolbar.setTitle("设置");
        }
        
        // 加载选中的Fragment
        return loadFragment(fragment);
    }
    
    /**
     * 加载Fragment
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
