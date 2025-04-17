package com.example.footprintexplorer.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footprintexplorer.databinding.ActivityTestBinding;
import com.example.footprintexplorer.utils.TestUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * 测试活动
 * 用于运行应用测试并显示测试结果
 */
public class TestActivity extends AppCompatActivity {

    private ActivityTestBinding binding;
    private boolean isTestRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用视图绑定
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("应用测试");
        
        // 设置按钮点击事件
        setupButtonListeners();
    }
    
    /**
     * 设置按钮点击事件
     */
    private void setupButtonListeners() {
        // 数据库测试按钮
        binding.buttonDatabaseTest.setOnClickListener(v -> {
            if (!isTestRunning) {
                runTest(() -> {
                    String result = TestUtils.testDatabasePerformance(this);
                    updateTestResult(result);
                });
            } else {
                showTestRunningMessage();
            }
        });
        
        // 位置计算测试按钮
        binding.buttonLocationTest.setOnClickListener(v -> {
            if (!isTestRunning) {
                runTest(() -> {
                    String result = TestUtils.testLocationCalculations(this);
                    updateTestResult(result);
                });
            } else {
                showTestRunningMessage();
            }
        });
        
        // UI性能测试按钮
        binding.buttonUiTest.setOnClickListener(v -> {
            if (!isTestRunning) {
                runTest(() -> {
                    String result = TestUtils.testUIPerformance(this);
                    updateTestResult(result);
                });
            } else {
                showTestRunningMessage();
            }
        });
        
        // 电池优化测试按钮
        binding.buttonBatteryTest.setOnClickListener(v -> {
            if (!isTestRunning) {
                runTest(() -> {
                    String result = TestUtils.testBatteryOptimization(this);
                    updateTestResult(result);
                });
            } else {
                showTestRunningMessage();
            }
        });
        
        // 全部测试按钮
        binding.buttonRunAllTests.setOnClickListener(v -> {
            if (!isTestRunning) {
                runTest(() -> {
                    String result = TestUtils.runAllTests(this);
                    updateTestResult(result);
                });
            } else {
                showTestRunningMessage();
            }
        });
        
        // 保存报告按钮
        binding.buttonSaveReport.setOnClickListener(v -> {
            saveTestReport();
        });
        
        // 清除结果按钮
        binding.buttonClearResults.setOnClickListener(v -> {
            binding.textTestResults.setText("");
            binding.buttonSaveReport.setEnabled(false);
        });
    }
    
    /**
     * 运行测试
     * @param testRunnable 测试任务
     */
    private void runTest(Runnable testRunnable) {
        // 显示进度条
        binding.progressBar.setVisibility(View.VISIBLE);
        isTestRunning = true;
        
        // 禁用按钮
        setButtonsEnabled(false);
        
        // 在后台线程中运行测试
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                testRunnable.run();
            } catch (Exception e) {
                // 更新UI显示错误信息
                runOnUiThread(() -> {
                    updateTestResult("测试出错: " + e.getMessage());
                });
            } finally {
                // 更新UI，隐藏进度条，启用按钮
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    setButtonsEnabled(true);
                    isTestRunning = false;
                });
            }
        });
    }
    
    /**
     * 更新测试结果
     * @param result 测试结果
     */
    private void updateTestResult(String result) {
        String currentText = binding.textTestResults.getText().toString();
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        
        // 添加时间戳和分隔线
        String newResult = currentText + 
                "\n[" + timestamp + "]\n" + 
                result + 
                "\n----------------------------------------\n";
        
        binding.textTestResults.setText(newResult);
        
        // 滚动到底部
        binding.scrollView.post(() -> binding.scrollView.fullScroll(View.FOCUS_DOWN));
        
        // 启用保存按钮
        binding.buttonSaveReport.setEnabled(true);
    }
    
    /**
     * 设置按钮启用状态
     * @param enabled 是否启用
     */
    private void setButtonsEnabled(boolean enabled) {
        binding.buttonDatabaseTest.setEnabled(enabled);
        binding.buttonLocationTest.setEnabled(enabled);
        binding.buttonUiTest.setEnabled(enabled);
        binding.buttonBatteryTest.setEnabled(enabled);
        binding.buttonRunAllTests.setEnabled(enabled);
        binding.buttonClearResults.setEnabled(enabled);
    }
    
    /**
     * 显示测试正在运行的提示
     */
    private void showTestRunningMessage() {
        Toast.makeText(this, "测试正在运行，请稍候", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 保存测试报告
     */
    private void saveTestReport() {
        String reportContent = binding.textTestResults.getText().toString();
        if (reportContent.isEmpty()) {
            Toast.makeText(this, "没有测试结果可保存", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            // 创建报告文件
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "test_report_" + timestamp + ".txt";
            
            File reportsDir = new File(getExternalFilesDir(null), "reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            File reportFile = new File(reportsDir, fileName);
            
            // 写入报告内容
            FileWriter writer = new FileWriter(reportFile);
            writer.write("足迹探索应用测试报告\n");
            writer.write("生成时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n\n");
            writer.write(reportContent);
            writer.close();
            
            // 显示成功消息
            Toast.makeText(this, "测试报告已保存至: " + reportFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "保存报告失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
