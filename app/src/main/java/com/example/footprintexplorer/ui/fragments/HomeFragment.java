package com.example.footprintexplorer.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.databinding.FragmentHomeBinding;
import com.example.footprintexplorer.services.LocationTrackingService;
import com.example.footprintexplorer.ui.activities.MainActivity;
import com.example.footprintexplorer.ui.viewmodels.MainViewModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

/**
 * 首页Fragment
 * 显示用户的追踪状态和控制按钮
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainViewModel viewModel;
    private static final int DEFAULT_UPDATE_INTERVAL = 10000; // 10秒

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        
        // 设置追踪按钮
        setupTrackingButton();
        
        // 观察追踪状态
        observeTrackingState();
        
        // 观察权限状态
        observePermissionState();
    }
    
    /**
     * 设置追踪按钮
     */
    private void setupTrackingButton() {
        binding.btnTracking.setOnClickListener(v -> {
            if (viewModel.isTracking().getValue() != null && viewModel.isTracking().getValue()) {
                // 停止追踪
                stopTracking();
            } else {
                // 开始追踪前检查权限
                checkLocationPermission();
            }
        });
        
        // 设置追踪模式选择
        binding.switchManual.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.textTrackingMode.setText(isChecked ? "手动记录模式" : "自动记录模式");
        });
        
        // 设置追踪间隔选择
        binding.seekbarInterval.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                // 间隔范围：5秒到5分钟
                int interval = (progress + 1) * 5000;
                binding.textInterval.setText(String.format("更新间隔: %d 秒", interval / 1000));
            }
            
            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });
    }
    
    /**
     * 观察追踪状态
     */
    private void observeTrackingState() {
        viewModel.isTracking().observe(getViewLifecycleOwner(), isTracking -> {
            if (isTracking) {
                binding.btnTracking.setText(R.string.stop_tracking);
                binding.btnTracking.setBackgroundResource(R.drawable.btn_stop);
                binding.textStatus.setText(R.string.tracking_active);
                binding.switchManual.setEnabled(false);
                binding.seekbarInterval.setEnabled(false);
            } else {
                binding.btnTracking.setText(R.string.start_tracking);
                binding.btnTracking.setBackgroundResource(R.drawable.btn_start);
                binding.textStatus.setText(R.string.tracking_inactive);
                binding.switchManual.setEnabled(true);
                binding.seekbarInterval.setEnabled(true);
            }
        });
    }
    
    /**
     * 观察权限状态
     */
    private void observePermissionState() {
        viewModel.arePermissionsGranted().observe(getViewLifecycleOwner(), granted -> {
            binding.btnTracking.setEnabled(granted);
            if (!granted) {
                binding.textStatus.setText("需要位置权限");
            }
        });
    }
    
    /**
     * 检查位置权限
     */
    private void checkLocationPermission() {
        Dexter.withContext(requireContext())
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            // 权限已授予，开始追踪
                            startTracking();
                        } else {
                            // 权限被拒绝
                            Toast.makeText(requireContext(), "需要位置权限才能记录足迹", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }
    
    /**
     * 开始追踪
     */
    private void startTracking() {
        boolean isManual = binding.switchManual.isChecked();
        int interval = (binding.seekbarInterval.getProgress() + 1) * 5000;
        
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).startTracking(isManual, interval);
            Toast.makeText(requireContext(), "开始记录足迹", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 停止追踪
     */
    private void stopTracking() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).stopTracking();
            Toast.makeText(requireContext(), "停止记录足迹", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
