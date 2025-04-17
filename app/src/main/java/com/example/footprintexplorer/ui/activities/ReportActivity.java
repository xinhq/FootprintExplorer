package com.example.footprintexplorer.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.databinding.ActivityReportBinding;
import com.example.footprintexplorer.ui.adapters.ReportPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

/**
 * 报告活动
 * 显示用户的周报告、月报告和年报告
 */
public class ReportActivity extends AppCompatActivity {

    private ActivityReportBinding binding;
    private ReportPagerAdapter adapter;
    private String[] tabTitles = {"周报告", "月报告", "年报告"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用视图绑定
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("我的足迹报告");
        }
        
        // 设置ViewPager和TabLayout
        setupViewPager();
        
        // 设置分享按钮
        binding.fabShare.setOnClickListener(v -> shareReport());
    }
    
    /**
     * 设置ViewPager和TabLayout
     */
    private void setupViewPager() {
        // 创建适配器
        adapter = new ReportPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        
        // 连接TabLayout和ViewPager
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
        
        // 设置页面切换监听器
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateReportTitle(position);
            }
        });
        
        // 默认选择当前时间最接近的报告类型
        selectDefaultTab();
    }
    
    /**
     * 根据当前时间选择默认标签页
     */
    private void selectDefaultTab() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        
        // 如果是周末或月初，显示相应的报告
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            binding.viewPager.setCurrentItem(0); // 周报告
        } else if (dayOfMonth <= 3) {
            binding.viewPager.setCurrentItem(1); // 月报告
        } else {
            binding.viewPager.setCurrentItem(0); // 默认周报告
        }
    }
    
    /**
     * 更新报告标题
     */
    private void updateReportTitle(int position) {
        Calendar calendar = Calendar.getInstance();
        String title = "";
        
        switch (position) {
            case 0: // 周报告
                title = "第" + calendar.get(Calendar.WEEK_OF_YEAR) + "周足迹报告";
                break;
            case 1: // 月报告
                title = (calendar.get(Calendar.MONTH) + 1) + "月足迹报告";
                break;
            case 2: // 年报告
                title = calendar.get(Calendar.YEAR) + "年足迹报告";
                break;
        }
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(title);
        }
    }
    
    /**
     * 分享报告
     */
    private void shareReport() {
        int position = binding.viewPager.getCurrentItem();
        String reportType = tabTitles[position];
        
        Toast.makeText(this, "分享" + reportType + "功能即将上线", Toast.LENGTH_SHORT).show();
        
        // 这里应该实现报告分享功能
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
