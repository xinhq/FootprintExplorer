package com.example.footprintexplorer.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.footprintexplorer.databinding.FragmentBadgesBinding;
import com.example.footprintexplorer.ui.adapters.BadgeAdapter;
import com.example.footprintexplorer.ui.viewmodels.BadgeViewModel;

/**
 * 徽章Fragment
 * 显示用户解锁的徽章
 */
public class BadgesFragment extends Fragment {

    private FragmentBadgesBinding binding;
    private BadgeViewModel viewModel;
    private BadgeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBadgesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(BadgeViewModel.class);
        
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
        
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
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
        viewModel.getBadges().observe(getViewLifecycleOwner(), badges -> {
            if (badges != null && !badges.isEmpty()) {
                adapter.submitList(badges);
                binding.emptyView.setVisibility(View.GONE);
            } else {
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
        
        // 观察加载状态
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
    
    /**
     * 显示徽章详情
     */
    private void showBadgeDetails(com.example.footprintexplorer.data.entity.Badge badge) {
        // 这里应该显示徽章详情对话框
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
