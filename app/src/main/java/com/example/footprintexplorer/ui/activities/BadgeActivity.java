package com.example.footprintexplorer.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.databinding.ActivityBadgeBinding;
import com.example.footprintexplorer.ui.adapters.BadgeAdapter;
import com.example.footprintexplorer.ui.viewmodels.BadgeViewModel;

/**
 * 徽章活动
 * 显示用户解锁的徽章和徽章详情
 */
public class BadgeActivity extends AppCompatActivity {

    private ActivityBadgeBinding binding;
    private BadgeViewModel viewModel;
    private BadgeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用视图绑定
        binding = ActivityBadgeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("我的徽章收藏");
        }
        
        // 设置ViewModel
        viewModel = new ViewModelProvider(this).get(BadgeViewModel.class);
        
        // 设置RecyclerView
        setupRecyclerView();
        
        // 设置过滤器
        setupFilters();
        
        // 观察数据变化
        observeViewModel();
    }
    
    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new BadgeAdapter(badge -> {
            // 点击徽章显示详情
            showBadgeDetails(badge);
        });
        
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.setAdapter(adapter);
    }
    
    /**
     * 设置过滤器
     */
    private void setupFilters() {
        binding.chipAll.setOnClickListener(v -> viewModel.filterBadges(null));
        binding.chipFood.setOnClickListener(v -> viewModel.filterBadges("美食"));
        binding.chipCulture.setOnClickListener(v -> viewModel.filterBadges("文物"));
        binding.chipAnimal.setOnClickListener(v -> viewModel.filterBadges("动物"));
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察徽章列表
        viewModel.getBadges().observe(this, badges -> {
            if (badges != null && !badges.isEmpty()) {
                adapter.submitList(badges);
                binding.emptyView.setVisibility(View.GONE);
            } else {
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
        
        // 观察加载状态
        viewModel.isLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
    
    /**
     * 显示徽章详情
     */
    private void showBadgeDetails(com.example.footprintexplorer.data.entity.Badge badge) {
        // 这里应该显示徽章详情对话框或跳转到徽章详情页面
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
