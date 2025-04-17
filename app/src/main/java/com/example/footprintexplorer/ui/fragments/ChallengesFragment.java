package com.example.footprintexplorer.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.data.entity.Challenge;
import com.example.footprintexplorer.databinding.FragmentChallengesBinding;
import com.example.footprintexplorer.ui.adapters.ChallengeAdapter;
import com.example.footprintexplorer.ui.viewmodels.ChallengeViewModel;

/**
 * 挑战任务Fragment
 * 显示游戏化挑战任务列表
 */
public class ChallengesFragment extends Fragment implements ChallengeAdapter.OnChallengeClickListener {

    private FragmentChallengesBinding binding;
    private ChallengeViewModel viewModel;
    private ChallengeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChallengesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ChallengeViewModel.class);
        
        // 设置RecyclerView
        setupRecyclerView();
        
        // 设置筛选器
        setupFilters();
        
        // 观察数据变化
        observeViewModel();
        
        // 设置刷新布局
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshChallenges();
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }
    
    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new ChallengeAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
    
    /**
     * 设置筛选器
     */
    private void setupFilters() {
        // 设置筛选器点击事件
        binding.chipAll.setOnClickListener(v -> viewModel.loadAllChallenges());
        binding.chipActive.setOnClickListener(v -> viewModel.loadActiveChallenges());
        binding.chipCompleted.setOnClickListener(v -> viewModel.loadCompletedChallenges());
        binding.chipDistance.setOnClickListener(v -> viewModel.loadChallengesByType("distance"));
        binding.chipPlace.setOnClickListener(v -> viewModel.loadChallengesByType("place"));
        binding.chipBadge.setOnClickListener(v -> viewModel.loadChallengesByType("badge"));
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察挑战任务列表
        viewModel.getChallenges().observe(getViewLifecycleOwner(), challenges -> {
            if (challenges != null) {
                adapter.submitList(challenges);
                
                // 更新空视图
                if (challenges.isEmpty()) {
                    binding.emptyView.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyView.setVisibility(View.GONE);
                }
            }
        });
        
        // 观察用户等级和经验值
        viewModel.getUserLevel().observe(getViewLifecycleOwner(), level -> {
            binding.textLevel.setText(getString(R.string.level_format, level));
        });
        
        viewModel.getUserXp().observe(getViewLifecycleOwner(), xp -> {
            binding.textXp.setText(getString(R.string.xp_format, xp));
        });
        
        viewModel.getUserXpProgress().observe(getViewLifecycleOwner(), progress -> {
            binding.progressXp.setProgress(progress);
        });
        
        viewModel.getUserXpToNextLevel().observe(getViewLifecycleOwner(), xpToNextLevel -> {
            binding.textXpToNextLevel.setText(getString(R.string.xp_to_next_level_format, xpToNextLevel));
        });
        
        // 观察加载状态
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                binding.emptyView.setVisibility(View.GONE);
            }
        });
    }
    
    @Override
    public void onChallengeClick(Challenge challenge) {
        // 显示挑战详情
        showChallengeDetails(challenge);
    }
    
    /**
     * 显示挑战详情
     */
    private void showChallengeDetails(Challenge challenge) {
        // 创建挑战详情对话框
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle(challenge.getTitle());
        
        // 构建详情内容
        StringBuilder content = new StringBuilder();
        content.append(challenge.getDescription()).append("\n\n");
        content.append("目标: ").append(challenge.getTarget()).append("\n");
        content.append("进度: ").append(challenge.getProgress()).append(" (")
                .append(challenge.getProgressPercentage()).append("%)\n");
        content.append("奖励: ").append(challenge.getXpReward()).append(" XP\n");
        content.append("难度: ");
        for (int i = 0; i < challenge.getDifficulty(); i++) {
            content.append("★");
        }
        content.append("\n");
        
        if (challenge.isCompleted() && challenge.getCompleteTime() != null) {
            content.append("完成时间: ").append(
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                            .format(challenge.getCompleteTime()));
        }
        
        builder.setMessage(content.toString());
        
        // 添加按钮
        if (!challenge.isCompleted()) {
            builder.setPositiveButton("放弃挑战", (dialog, which) -> {
                viewModel.abandonChallenge(challenge);
                Toast.makeText(requireContext(), "已放弃挑战", Toast.LENGTH_SHORT).show();
            });
        }
        
        builder.setNegativeButton("关闭", (dialog, which) -> dialog.dismiss());
        
        // 显示对话框
        builder.create().show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
