package com.example.footprintexplorer.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.footprintexplorer.R;
import com.example.footprintexplorer.data.entity.Badge;
import com.example.footprintexplorer.data.entity.Place;
import com.example.footprintexplorer.ui.viewmodels.ReportViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 报告导出工具类
 * 用于将报告导出为PDF或图片
 */
public class ReportExportUtils {

    /**
     * 导出报告为PDF
     */
    public static void exportReportToPdf(Context context, ReportViewModel.ReportData reportData, String reportType) {
        // 创建PDF文档
        PdfDocument document = new PdfDocument();
        
        // 创建页面
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        
        // 获取画布
        Canvas canvas = page.getCanvas();
        
        // 绘制报告内容
        drawReportContent(canvas, context, reportData, reportType);
        
        // 结束页面
        document.finishPage(page);
        
        // 保存PDF文件
        String fileName = generateFileName(reportType, "pdf");
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        
        try {
            document.writeTo(new FileOutputStream(file));
            document.close();
            
            // 分享PDF文件
            sharePdfFile(context, file);
            
            Toast.makeText(context, "报告已导出为PDF", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "导出PDF失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 导出报告为图片
     */
    public static void exportReportToImage(Context context, View reportView, String reportType) {
        // 创建位图
        Bitmap bitmap = Bitmap.createBitmap(reportView.getWidth(), reportView.getHeight(), Bitmap.Config.ARGB_8888);
        
        // 获取画布
        Canvas canvas = new Canvas(bitmap);
        
        // 绘制视图
        reportView.draw(canvas);
        
        // 保存图片文件
        String fileName = generateFileName(reportType, "png");
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            
            // 分享图片文件
            shareImageFile(context, file);
            
            Toast.makeText(context, "报告已导出为图片", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "导出图片失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 绘制报告内容
     */
    private static void drawReportContent(Canvas canvas, Context context, ReportViewModel.ReportData reportData, String reportType) {
        // 设置画笔
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.rgb(33, 150, 243));
        titlePaint.setTextSize(24);
        titlePaint.setFakeBoldText(true);
        
        Paint subtitlePaint = new Paint();
        subtitlePaint.setColor(Color.rgb(33, 33, 33));
        subtitlePaint.setTextSize(18);
        subtitlePaint.setFakeBoldText(true);
        
        Paint textPaint = new Paint();
        textPaint.setColor(Color.rgb(33, 33, 33));
        textPaint.setTextSize(14);
        
        // 绘制标题
        String title = "足迹探索 - " + reportType + "报告";
        canvas.drawText(title, 50, 50, titlePaint);
        
        // 绘制日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = "生成日期: " + dateFormat.format(new Date());
        canvas.drawText(date, 50, 80, textPaint);
        
        // 绘制分隔线
        Paint linePaint = new Paint();
        linePaint.setColor(Color.rgb(200, 200, 200));
        linePaint.setStrokeWidth(1);
        canvas.drawLine(50, 100, 545, 100, linePaint);
        
        // 绘制统计数据
        canvas.drawText("统计数据", 50, 130, subtitlePaint);
        canvas.drawText("总行程: " + String.format("%.1f 公里", reportData.getTotalDistance() / 1000), 50, 160, textPaint);
        canvas.drawText("解锁地点: " + reportData.getPlaces().size() + " 个", 50, 180, textPaint);
        canvas.drawText("获得徽章: " + reportData.getBadges().size() + " 个", 50, 200, textPaint);
        
        // 绘制分隔线
        canvas.drawLine(50, 220, 545, 220, linePaint);
        
        // 绘制地点列表
        canvas.drawText("解锁地点", 50, 250, subtitlePaint);
        List<Place> places = reportData.getPlaces();
        int y = 280;
        for (int i = 0; i < places.size() && i < 10; i++) {
            Place place = places.get(i);
            canvas.drawText((i + 1) + ". " + place.getName() + " (" + place.getCategory() + ")", 50, y, textPaint);
            y += 20;
        }
        
        // 如果地点太多，显示省略号
        if (places.size() > 10) {
            canvas.drawText("...", 50, y, textPaint);
            y += 20;
        }
        
        // 绘制分隔线
        canvas.drawLine(50, y, 545, y, linePaint);
        y += 30;
        
        // 绘制徽章列表
        canvas.drawText("获得徽章", 50, y, subtitlePaint);
        y += 30;
        List<Badge> badges = reportData.getBadges();
        for (int i = 0; i < badges.size() && i < 10; i++) {
            Badge badge = badges.get(i);
            canvas.drawText((i + 1) + ". " + badge.getName() + " (" + badge.getCategory() + ")", 50, y, textPaint);
            y += 20;
        }
        
        // 如果徽章太多，显示省略号
        if (badges.size() > 10) {
            canvas.drawText("...", 50, y, textPaint);
            y += 20;
        }
        
        // 绘制分隔线
        canvas.drawLine(50, y, 545, y, linePaint);
        y += 30;
        
        // 绘制页脚
        canvas.drawText("由足迹探索应用生成", 50, 800, textPaint);
    }
    
    /**
     * 生成文件名
     */
    private static String generateFileName(String reportType, String extension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());
        return "FootprintExplorer_" + reportType + "_" + timestamp + "." + extension;
    }
    
    /**
     * 分享PDF文件
     */
    private static void sharePdfFile(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        context.startActivity(Intent.createChooser(intent, "分享报告"));
    }
    
    /**
     * 分享图片文件
     */
    private static void shareImageFile(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        context.startActivity(Intent.createChooser(intent, "分享报告"));
    }
}
