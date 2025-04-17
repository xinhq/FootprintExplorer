package com.example.footprintexplorer.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.databinding.FragmentSettingsBinding;
import com.example.footprintexplorer.ui.viewmodels.SettingsViewModel;

/**
 * 设置Fragment
 * 用于个性化设置和应用配置
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        
        // 观察设置数据
        observeSettings();
        
        // 设置监听器
        setupListeners();
    }
    
    /**
     * 观察设置数据
     */
    private void observeSettings() {
        // 观察用户信息
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            binding.editUsername.setText(username);
        });
        
        // 观察主题设置
        viewModel.getTheme().observe(getViewLifecycleOwner(), theme -> {
            switch (theme) {
                case "light":
                    binding.radioLight.setChecked(true);
                    break;
                case "dark":
                    binding.radioDark.setChecked(true);
                    break;
                default:
                    binding.radioSystem.setChecked(true);
                    break;
            }
        });
        
        // 观察地图类型设置
        viewModel.getMapType().observe(getViewLifecycleOwner(), mapType -> {
            switch (mapType) {
                case "satellite":
                    binding.radioSatellite.setChecked(true);
                    break;
                case "terrain":
                    binding.radioTerrain.setChecked(true);
                    break;
                default:
                    binding.radioNormal.setChecked(true);
                    break;
            }
        });
        
        // 观察通知设置
        viewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchNotifications.setChecked(enabled);
        });
        
        // 观察自动追踪设置
        viewModel.getAutoTracking().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchAutoTracking.setChecked(enabled);
        });
        
        // 观察追踪间隔设置
        viewModel.getTrackingInterval().observe(getViewLifecycleOwner(), interval -> {
            binding.sliderInterval.setValue(interval);
            binding.textIntervalValue.setText(String.format("%d 秒", interval));
        });
    }
    
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 用户名保存按钮
        binding.buttonSaveUsername.setOnClickListener(v -> {
            String username = binding.editUsername.getText().toString().trim();
            if (!username.isEmpty()) {
                viewModel.setUsername(username);
                Toast.makeText(requireContext(), "用户名已保存", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 主题设置
        binding.radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_light) {
                viewModel.setTheme("light");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.radio_dark) {
                viewModel.setTheme("dark");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                viewModel.setTheme("system");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });
        
        // 地图类型设置
        binding.radioGroupMap.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_satellite) {
                viewModel.setMapType("satellite");
            } else if (checkedId == R.id.radio_terrain) {
                viewModel.setMapType("terrain");
            } else {
                viewModel.setMapType("normal");
            }
        });
        
        // 通知设置
        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setNotificationsEnabled(isChecked);
        });
        
        // 自动追踪设置
        binding.switchAutoTracking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setAutoTracking(isChecked);
            // 如果启用自动追踪，检查权限
            if (isChecked) {
                checkLocationPermissions();
            }
        });
        
        // 追踪间隔设置
        binding.sliderInterval.addOnChangeListener((slider, value, fromUser) -> {
            int interval = (int) value;
            viewModel.setTrackingInterval(interval);
            binding.textIntervalValue.setText(String.format("%d 秒", interval));
        });
        
        // 清除数据按钮
        binding.buttonClearData.setOnClickListener(v -> {
            showClearDataConfirmDialog();
        });
    }
    
    /**
     * 检查位置权限
     */
    private void checkLocationPermissions() {
        // 在实际应用中，这里应该检查位置权限
        // 如果没有权限，应该请求权限
        // 这里简化处理，只显示提示
        Toast.makeText(requireContext(), "请确保已授予位置权限", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 显示清除数据确认对话框
     */
    private void showClearDataConfirmDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("清除数据");
        builder.setMessage("确定要清除所有足迹数据吗？此操作不可撤销。");
        builder.setPositiveButton("确定", (dialog, which) -> {
            viewModel.clearAllData();
            Toast.makeText(requireContext(), "数据已清除", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
