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
import com.example.footprintexplorer.databinding.FragmentReportsBinding;
import com.example.footprintexplorer.ui.activities.ReportActivity;
import com.example.footprintexplorer.ui.viewmodels.ReportViewModel;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * 报告Fragment
 * 显示用户的活动统计和报告入口
 */
public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private ReportViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);
        
        // 设置报告卡片点击事件
        setupReportCards();
        
        // 设置图表
        setupCharts();
        
        // 观察数据变化
        observeViewModel();
    }
    
    /**
     * 设置报告卡片点击事件
     */
    private void setupReportCards() {
        binding.cardWeekly.setOnClickListener(v -> openReportActivity(0));
        binding.cardMonthly.setOnClickListener(v -> openReportActivity(1));
        binding.cardYearly.setOnClickListener(v -> openReportActivity(2));
    }
    
    /**
     * 打开报告活动
     */
    private void openReportActivity(int reportType) {
        android.content.Intent intent = new android.content.Intent(requireContext(), ReportActivity.class);
        intent.putExtra("report_type", reportType);
        startActivity(intent);
    }
    
    /**
     * 设置图表
     */
    private void setupCharts() {
        // 设置距离图表
        setupDistanceChart();
        
        // 设置地点图表
        setupPlacesChart();
    }
    
    /**
     * 设置距离图表
     */
    private void setupDistanceChart() {
        // 配置图表样式
        binding.chartDistance.getDescription().setEnabled(false);
        binding.chartDistance.setDrawGridBackground(false);
        binding.chartDistance.setDrawBarShadow(false);
        binding.chartDistance.setHighlightFullBarEnabled(false);
        
        // 配置X轴
        XAxis xAxis = binding.chartDistance.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        
        // 配置Y轴
        binding.chartDistance.getAxisLeft().setDrawGridLines(true);
        binding.chartDistance.getAxisRight().setEnabled(false);
        
        // 配置图例
        binding.chartDistance.getLegend().setEnabled(false);
    }
    
    /**
     * 设置地点图表
     */
    private void setupPlacesChart() {
        // 配置图表样式
        binding.chartPlaces.getDescription().setEnabled(false);
        binding.chartPlaces.setDrawGridBackground(false);
        binding.chartPlaces.setDrawBarShadow(false);
        binding.chartPlaces.setHighlightFullBarEnabled(false);
        
        // 配置X轴
        XAxis xAxis = binding.chartPlaces.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(3);
        
        // 配置Y轴
        binding.chartPlaces.getAxisLeft().setDrawGridLines(true);
        binding.chartPlaces.getAxisRight().setEnabled(false);
        
        // 配置图例
        binding.chartPlaces.getLegend().setEnabled(false);
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察每周距离数据
        viewModel.getWeeklyDistances().observe(getViewLifecycleOwner(), distances -> {
            if (distances != null) {
                updateDistanceChart(distances);
            }
        });
        
        // 观察地点类型数据
        viewModel.getPlaceTypeCount().observe(getViewLifecycleOwner(), placeCounts -> {
            if (placeCounts != null) {
                updatePlacesChart(placeCounts);
            }
        });
        
        // 观察统计数据
        viewModel.getTotalDistance().observe(getViewLifecycleOwner(), distance -> {
            binding.textTotalDistance.setText(String.format("%.1f 公里", distance / 1000));
        });
        
        viewModel.getTotalPlaces().observe(getViewLifecycleOwner(), places -> {
            binding.textTotalPlaces.setText(String.valueOf(places));
        });
        
        viewModel.getTotalBadges().observe(getViewLifecycleOwner(), badges -> {
            binding.textTotalBadges.setText(String.valueOf(badges));
        });
    }
    
    /**
     * 更新距离图表
     */
    private void updateDistanceChart(List<Float> distances) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < distances.size(); i++) {
            entries.add(new BarEntry(i, distances.get(i) / 1000)); // 转换为公里
        }
        
        BarDataSet dataSet = new BarDataSet(entries, "每日距离");
        dataSet.setColor(getResources().getColor(R.color.primary, null));
        
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        
        binding.chartDistance.setData(data);
        
        // 设置X轴标签
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        binding.chartDistance.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        
        binding.chartDistance.invalidate();
    }
    
    /**
     * 更新地点图表
     */
    private void updatePlacesChart(List<Integer> placeCounts) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < placeCounts.size(); i++) {
            entries.add(new BarEntry(i, placeCounts.get(i)));
        }
        
        BarDataSet dataSet = new BarDataSet(entries, "地点类型");
        dataSet.setColors(new int[]{
                getResources().getColor(R.color.badge_food, null),
                getResources().getColor(R.color.badge_culture, null),
                getResources().getColor(R.color.badge_animal, null)
        });
        
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        
        binding.chartPlaces.setData(data);
        
        // 设置X轴标签
        String[] types = {"美食", "文物", "动物"};
        binding.chartPlaces.getXAxis().setValueFormatter(new IndexAxisValueFormatter(types));
        
        binding.chartPlaces.invalidate();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
