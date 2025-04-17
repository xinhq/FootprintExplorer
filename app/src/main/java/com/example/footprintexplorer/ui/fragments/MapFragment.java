package com.example.footprintexplorer.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.databinding.FragmentMapBinding;
import com.example.footprintexplorer.ui.viewmodels.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * 地图Fragment
 * 显示用户的足迹和解锁的地点
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private MapViewModel viewModel;
    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        
        // 初始化地图
        initMap();
        
        // 设置过滤器
        setupFilters();
        
        // 设置日期选择器
        setupDatePicker();
    }
    
    /**
     * 初始化地图
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    
    /**
     * 设置过滤器
     */
    private void setupFilters() {
        binding.chipAll.setOnClickListener(v -> viewModel.filterTracks(null));
        binding.chipToday.setOnClickListener(v -> viewModel.filterTracks("today"));
        binding.chipWeek.setOnClickListener(v -> viewModel.filterTracks("week"));
        binding.chipMonth.setOnClickListener(v -> viewModel.filterTracks("month"));
    }
    
    /**
     * 设置日期选择器
     */
    private void setupDatePicker() {
        binding.btnDatePicker.setOnClickListener(v -> {
            // 显示日期选择器
            showDatePicker();
        });
    }
    
    /**
     * 显示日期选择器
     */
    private void showDatePicker() {
        // 这里应该显示日期选择器对话框
    }
    
    /**
     * 当地图准备好时调用
     */
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        
        // 设置地图类型
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        // 启用我的位置层
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        
        // 启用地图控件
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        // 观察轨迹数据
        observeTrackData();
        
        // 观察地点数据
        observePlaceData();
    }
    
    /**
     * 观察轨迹数据
     */
    private void observeTrackData() {
        viewModel.getTracks().observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null && !tracks.isEmpty() && googleMap != null) {
                // 清除现有轨迹
                googleMap.clear();
                
                // 绘制轨迹
                PolylineOptions polylineOptions = new PolylineOptions()
                        .color(getResources().getColor(R.color.primary, null))
                        .width(10);
                
                for (LatLng point : tracks) {
                    polylineOptions.add(point);
                }
                
                googleMap.addPolyline(polylineOptions);
                
                // 移动相机到最后一个点
                if (!tracks.isEmpty()) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tracks.get(tracks.size() - 1), 15));
                }
            }
        });
    }
    
    /**
     * 观察地点数据
     */
    private void observePlaceData() {
        viewModel.getPlaces().observe(getViewLifecycleOwner(), places -> {
            if (places != null && !places.isEmpty() && googleMap != null) {
                // 添加地点标记
                for (com.example.footprintexplorer.data.entity.Place place : places) {
                    LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
                    googleMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(place.getName())
                            .snippet(place.getFullAddress()));
                }
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
