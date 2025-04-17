package com.example.footprintexplorer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.databinding.ActivitySplashBinding;

/**
 * 启动页活动
 * 显示应用启动画面，并在一定时间后跳转到主界面
 */
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private static final long SPLASH_DELAY = 2000; // 2秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用视图绑定
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 延迟跳转到主界面
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToMainActivity, SPLASH_DELAY);
    }
    
    /**
     * 跳转到主界面
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 结束当前活动，防止用户返回
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
