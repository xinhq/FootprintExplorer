# 足迹探索(FootprintExplorer) 开发文档

## 1. 项目概述

足迹探索(FootprintExplorer)是一款安卓应用，用于记录用户的足迹，生成报告，展示活动轨迹，并通过徽章系统激励用户探索新地点。本文档详细介绍了应用的架构设计、技术实现和开发指南。

### 1.1 技术栈

- **开发语言**：Java
- **开发环境**：Android Studio
- **最低支持版本**：Android 6.0 (API 23)
- **目标版本**：Android 13 (API 33)
- **主要框架**：
  - Android Jetpack (Room, ViewModel, LiveData)
  - Google Maps API
  - MPAndroidChart (图表可视化)

### 1.2 项目结构

```
FootprintExplorer/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/footprintexplorer/
│   │   │   │   ├── data/               # 数据层
│   │   │   │   │   ├── database/       # 数据库相关
│   │   │   │   │   ├── entity/         # 实体类
│   │   │   │   │   ├── dao/            # 数据访问对象
│   │   │   │   │   └── converters/     # 类型转换器
│   │   │   │   ├── services/           # 服务
│   │   │   │   ├── ui/                 # 用户界面
│   │   │   │   │   ├── activities/     # 活动
│   │   │   │   │   ├── fragments/      # 片段
│   │   │   │   │   ├── adapters/       # 适配器
│   │   │   │   │   └── viewmodels/     # 视图模型
│   │   │   │   └── utils/              # 工具类
│   │   │   ├── res/                    # 资源文件
│   │   │   └── AndroidManifest.xml     # 应用清单
│   │   └── test/                       # 测试代码
│   └── build.gradle                    # 应用级构建配置
├── build.gradle                        # 项目级构建配置
└── docs/                               # 文档
```

## 2. 架构设计

### 2.1 整体架构

应用采用MVVM(Model-View-ViewModel)架构模式，结合Repository模式进行数据管理。

![架构图](architecture_diagram.png)

主要组件：
- **View层**：Activities, Fragments, Adapters
- **ViewModel层**：各功能的ViewModel类
- **Repository层**：数据仓库，协调本地数据和远程数据
- **Model层**：Room数据库和实体类

### 2.2 数据流

1. 用户交互触发UI事件
2. UI事件传递给ViewModel
3. ViewModel通过Repository请求或更新数据
4. Repository从本地数据库或远程服务获取数据
5. 数据通过LiveData返回给ViewModel
6. ViewModel更新UI状态
7. UI根据状态更新显示

## 3. 核心模块详解

### 3.1 数据库模块

#### 3.1.1 数据库设计

使用Room持久化库实现本地数据存储，主要包含以下表：

- **LocationRecord**：位置记录表
- **TrackingSession**：追踪会话表
- **Place**：地点表
- **Badge**：徽章表
- **Challenge**：挑战任务表
- **User**：用户表

#### 3.1.2 实体关系

![实体关系图](er_diagram.png)

主要关系：
- 一个TrackingSession包含多个LocationRecord (1:N)
- 一个Place可以关联多个Badge (1:N)
- 一个User可以完成多个Challenge (N:M)

#### 3.1.3 关键代码

**FootprintDatabase.java**
```java
@Database(
    entities = {
        LocationRecord.class,
        TrackingSession.class,
        Place.class,
        Badge.class,
        Challenge.class,
        User.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class FootprintDatabase extends RoomDatabase {
    
    private static volatile FootprintDatabase INSTANCE;
    
    public abstract LocationDao locationDao();
    public abstract TrackingSessionDao trackingSessionDao();
    public abstract PlaceDao placeDao();
    public abstract BadgeDao badgeDao();
    public abstract ChallengeDao challengeDao();
    public abstract UserDao userDao();
    
    // 单例模式获取数据库实例
    public static FootprintDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (FootprintDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            FootprintDatabase.class,
                            "footprint_database.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

### 3.2 位置追踪模块

#### 3.2.1 位置服务设计

位置追踪服务(LocationTrackingService)是一个前台服务，负责持续获取和记录用户位置。

主要功能：
- 使用LocationManager获取GPS和网络位置
- 根据电池状态动态调整位置更新频率
- 将位置数据保存到数据库
- 检测新地点并触发徽章解锁

#### 3.2.2 电池优化策略

为延长电池寿命，实现了多级电池优化策略：
- 根据电池电量调整位置更新频率和距离
- 低电量时自动切换到省电模式
- 智能位置记录，仅在有意义的位置变化时记录

#### 3.2.3 关键代码

**LocationTrackingService.java (部分)**
```java
@Override
public void onLocationChanged(Location location) {
    if (!isTracking || currentSessionId == -1) {
        return;
    }
    
    Log.d(TAG, "位置更新: " + location.getLatitude() + ", " + location.getLongitude());
    
    // 计算距离
    if (lastLocation != null) {
        float distance = lastLocation.distanceTo(location);
        totalDistance += distance;
        
        // 检查是否应该记录此位置点（根据电池状态）
        if (BatteryOptimizer.shouldRecordLocation(this, lastLocation, location)) {
            saveLocationRecord(location);
        } else {
            Log.d(TAG, "跳过记录位置点（电池优化）");
        }
        
        // 更新通知
        String distanceText = String.format("已行进 %.2f 公里", totalDistance / 1000);
        updateNotification(distanceText);
    } else {
        // 第一个位置点，直接记录
        saveLocationRecord(location);
    }
    
    lastLocation = location;
}
```

### 3.3 地图与可视化模块

#### 3.3.1 地图集成

使用Google Maps API实现地图功能，主要包括：
- 显示用户足迹轨迹
- 标记已访问地点和徽章位置
- 支持多种地图类型和交互操作

#### 3.3.2 数据可视化

使用MPAndroidChart库实现数据可视化，包括：
- 距离和时间趋势图
- 活动类型分布饼图
- 足迹热力图

#### 3.3.3 关键代码

**MapFragment.java (部分)**
```java
private void drawTrackOnMap(List<LocationRecord> records) {
    if (records == null || records.isEmpty() || googleMap == null) {
        return;
    }
    
    PolylineOptions polylineOptions = new PolylineOptions()
            .width(10)
            .color(ContextCompat.getColor(requireContext(), R.color.track_line));
    
    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
    
    for (LocationRecord record : records) {
        LatLng position = new LatLng(record.getLatitude(), record.getLongitude());
        polylineOptions.add(position);
        boundsBuilder.include(position);
    }
    
    googleMap.addPolyline(polylineOptions);
    
    // 调整地图视图以显示整个轨迹
    try {
        LatLngBounds bounds = boundsBuilder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    } catch (Exception e) {
        Log.e(TAG, "Error adjusting camera: " + e.getMessage());
    }
}
```

### 3.4 报告生成模块

#### 3.4.1 报告类型

实现了三种报告类型：
- 周报告：显示一周内的活动数据
- 月报告：显示一个月内的活动数据
- 年报告：显示一年内的活动数据

#### 3.4.2 报告内容

每种报告包含以下内容：
- 活动摘要（总距离、活动次数、活动时长）
- 地点统计（访问地点数量、新解锁地点）
- 活动图表（距离和时间趋势图）
- 足迹热图（活动密度分布）

#### 3.4.3 导出功能

支持将报告导出为PDF或图片格式，并提供分享功能。

#### 3.4.4 关键代码

**ReportViewModel.java (部分)**
```java
public LiveData<ReportData> generateWeeklyReport() {
    MutableLiveData<ReportData> reportLiveData = new MutableLiveData<>();
    
    Executors.newSingleThreadExecutor().execute(() -> {
        // 获取一周内的数据
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        long startTime = calendar.getTimeInMillis();
        long endTime = System.currentTimeMillis();
        
        // 查询会话数据
        List<TrackingSession> sessions = database.trackingSessionDao()
                .getSessionsBetweenSync(startTime, endTime);
        
        // 计算统计数据
        float totalDistance = 0;
        long totalDuration = 0;
        int sessionCount = sessions.size();
        
        for (TrackingSession session : sessions) {
            totalDistance += session.getDistance();
            totalDuration += (session.getEndTime() - session.getStartTime());
        }
        
        // 查询地点数据
        List<Place> places = database.placeDao()
                .getPlacesDiscoveredBetweenSync(startTime, endTime);
        
        // 创建报告数据对象
        ReportData reportData = new ReportData();
        reportData.setReportType(ReportData.TYPE_WEEKLY);
        reportData.setStartTime(startTime);
        reportData.setEndTime(endTime);
        reportData.setTotalDistance(totalDistance);
        reportData.setTotalDuration(totalDuration);
        reportData.setSessionCount(sessionCount);
        reportData.setPlaceCount(places.size());
        
        // 设置日期标签
        String[] dateLabels = new String[7];
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6);
        
        for (int i = 0; i < 7; i++) {
            dateLabels[i] = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        reportData.setDateLabels(dateLabels);
        
        // 设置每日距离数据
        float[] dailyDistances = new float[7];
        for (TrackingSession session : sessions) {
            Calendar sessionCal = Calendar.getInstance();
            sessionCal.setTimeInMillis(session.getStartTime());
            int dayIndex = 6 - (int)((endTime - session.getStartTime()) / (24 * 60 * 60 * 1000));
            if (dayIndex >= 0 && dayIndex < 7) {
                dailyDistances[dayIndex] += session.getDistance();
            }
        }
        reportData.setDailyDistances(dailyDistances);
        
        // 发布报告数据
        reportLiveData.postValue(reportData);
    });
    
    return reportLiveData;
}
```

### 3.5 徽章与游戏化模块

#### 3.5.1 徽章系统

设计了多种类型的徽章：
- 地区徽章：解锁新的城市或区县
- 美食徽章：探索当地特色美食地点
- 文化徽章：参观博物馆、历史遗迹等
- 自然徽章：探索公园、山脉、湖泊等
- 活动徽章：完成特定距离或时间的活动

#### 3.5.2 挑战任务

实现了多种挑战任务：
- 探索挑战：探索特定数量的新地点
- 距离挑战：在特定时间内完成目标距离
- 连续挑战：连续多天记录活动
- 主题挑战：探索特定主题的地点

#### 3.5.3 关键代码

**LocationUtils.java (部分)**
```java
public static void checkForNewPlace(Context context, Location location, FootprintDatabase database) {
    Executors.newSingleThreadExecutor().execute(() -> {
        // 检查是否为新地点
        boolean isNewPlace = isNewLocation(location, database);
        
        if (isNewPlace) {
            // 获取地点信息
            PlaceInfo placeInfo = getPlaceInfo(context, location);
            
            // 保存新地点
            Place place = new Place();
            place.setName(placeInfo.getName());
            place.setLatitude(location.getLatitude());
            place.setLongitude(location.getLongitude());
            place.setType(placeInfo.getType());
            place.setDiscoveryTime(System.currentTimeMillis());
            
            long placeId = database.placeDao().insert(place);
            
            // 检查是否解锁新徽章
            checkForNewBadges(context, placeInfo, placeId, database);
        }
    });
}

private static void checkForNewBadges(Context context, PlaceInfo placeInfo, long placeId, FootprintDatabase database) {
    // 根据地点类型创建对应徽章
    Badge badge = new Badge();
    badge.setPlaceId(placeId);
    badge.setName(placeInfo.getName() + "徽章");
    badge.setDescription("探索" + placeInfo.getName() + "获得的徽章");
    badge.setType(getBadgeTypeFromPlaceType(placeInfo.getType()));
    badge.setUnlockTime(System.currentTimeMillis());
    
    long badgeId = database.badgeDao().insert(badge);
    
    // 发送徽章解锁通知
    if (badgeId > 0) {
        sendBadgeNotification(context, badge);
    }
}
```

## 4. 性能优化

### 4.1 数据库优化

- 使用索引加速查询
- 异步操作避免主线程阻塞
- 分页加载大量数据
- 使用事务处理批量操作

### 4.2 电池优化

- 根据电池状态动态调整位置更新频率
- 智能位置记录算法
- 低电量时自动切换到省电模式
- 优化后台服务资源使用

### 4.3 内存优化

- 使用ViewHolder模式优化列表
- 图片资源合理缓存和回收
- 避免内存泄漏（如Activity引用）
- 大对象使用弱引用

### 4.4 性能监控

实现了性能监控工具类，用于：
- 监控应用内存使用
- 测量操作执行时间
- 检查数据库大小
- 监控电池使用情况

## 5. 测试

### 5.1 测试策略

采用多层次测试策略：
- 单元测试：测试各个组件的独立功能
- 集成测试：测试组件间的交互
- UI测试：测试用户界面和交互
- 性能测试：测试应用在各种条件下的性能

### 5.2 测试工具

- JUnit：单元测试框架
- Espresso：UI测试框架
- Mockito：模拟对象框架
- 自定义测试工具类：性能和功能测试

### 5.3 测试用例示例

**数据库测试**
```java
@Test
public void insertAndGetLocationRecord() {
    // 创建测试数据
    LocationRecord record = new LocationRecord();
    record.setSessionId(1);
    record.setLatitude(39.9);
    record.setLongitude(116.3);
    record.setTimestamp(System.currentTimeMillis());
    
    // 插入数据
    long recordId = locationDao.insert(record);
    
    // 验证插入成功
    assertTrue(recordId > 0);
    
    // 查询数据
    LocationRecord retrieved = locationDao.getLocationByIdSync(recordId);
    
    // 验证查询结果
    assertNotNull(retrieved);
    assertEquals(record.getLatitude(), retrieved.getLatitude(), 0.001);
    assertEquals(record.getLongitude(), retrieved.getLongitude(), 0.001);
}
```

## 6. 部署与发布

### 6.1 构建配置

**app/build.gradle**
```gradle
android {
    compileSdkVersion 33
    defaultConfig {
        applicationId "com.example.footprintexplorer"
        minSdkVersion 23
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Room数据库
    implementation 'androidx.room:room-runtime:2.5.2'
    annotationProcessor 'androidx.room:room-compiler:2.5.2'
    
    // ViewModel和LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.6.1'
    
    // Google Maps
    implementation 'com.google.android.gms:play-services-maps:18.1.0'
    implementation 'com.google.android.gms:play-services-location:21.0.1'
    
    // 图表库
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    
    // 测试依赖
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

### 6.2 签名配置

为发布版本配置签名：

```gradle
android {
    signingConfigs {
        release {
            storeFile file("keystore/release.keystore")
            storePassword "your_keystore_password"
            keyAlias "your_key_alias"
            keyPassword "your_key_password"
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            // ...
        }
    }
}
```

### 6.3 发布流程

1. 版本号更新
2. 运行测试确保质量
3. 生成签名APK
4. 上传到Google Play商店
5. 发布更新说明

## 7. 扩展与未来计划

### 7.1 潜在功能扩展

- **社交功能增强**：好友系统、足迹分享社区
- **AR功能**：增强现实展示徽章和地点信息
- **离线地图**：支持下载地图供离线使用
- **多设备同步**：跨设备数据同步
- **健康集成**：与健康应用集成，提供更全面的活动数据

### 7.2 技术改进计划

- 迁移到Kotlin语言
- 采用Jetpack Compose构建UI
- 引入依赖注入框架（如Dagger/Hilt）
- 实现更完善的CI/CD流程
- 添加更全面的分析和崩溃报告

## 8. 附录

### 8.1 API参考

#### Google Maps API

使用Google Maps API需要在Google Cloud Console创建项目并获取API密钥。

**配置步骤**：
1. 访问Google Cloud Console
2. 创建项目并启用Maps SDK for Android
3. 创建API密钥并限制使用范围
4. 在AndroidManifest.xml中添加密钥

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY" />
```

#### 位置API

使用FusedLocationProviderClient获取更准确的位置：

```java
FusedLocationProviderClient fusedLocationClient = 
    LocationServices.getFusedLocationProviderClient(context);

LocationRequest locationRequest = LocationRequest.create()
    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    .setInterval(10000)
    .setFastestInterval(5000);

fusedLocationClient.requestLocationUpdates(locationRequest, 
    locationCallback, Looper.getMainLooper());
```

### 8.2 常用工具类

#### DateUtils

```java
public class DateUtils {
    
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    public static String getRelativeTimeSpan(long timestamp) {
        return DateUtils.getRelativeTimeSpanString(
                timestamp, 
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS).toString();
    }
    
    public static long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    public static long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}
```

### 8.3 数据库架构

**数据库版本历史**：

| 版本 | 变更内容 | 迁移策略 |
|------|----------|----------|
| 1    | 初始版本 | N/A      |
| 2    | 添加Challenge和User表 | 自动迁移 |
| 3    | 在LocationRecord表中添加accuracy字段 | 自动迁移 |

**迁移示例**：

```java
static final Migration MIGRATION_2_3 = new Migration(2, 3) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE LocationRecord ADD COLUMN accuracy REAL NOT NULL DEFAULT 0");
    }
};
```

### 8.4 故障排除指南

**常见问题与解决方案**：

1. **数据库访问错误**
   - 症状：应用崩溃，日志显示数据库访问错误
   - 原因：在主线程执行数据库操作
   - 解决：确保所有数据库操作在后台线程执行

2. **位置更新失败**
   - 症状：无法获取位置更新
   - 原因：缺少位置权限或GPS未启用
   - 解决：检查权限请求和GPS状态

3. **内存泄漏**
   - 症状：应用长时间运行后内存使用增加
   - 原因：Activity或Fragment引用未正确释放
   - 解决：使用弱引用和正确的生命周期管理

4. **地图加载失败**
   - 症状：地图显示为灰色或空白
   - 原因：API密钥无效或网络问题
   - 解决：验证API密钥和网络连接

---

本文档由开发团队维护，最后更新于2025年4月14日。
