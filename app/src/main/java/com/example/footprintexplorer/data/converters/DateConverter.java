package com.example.footprintexplorer.data.converters;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * 日期转换器
 * 用于Room数据库中Date类型与Long类型的相互转换
 */
public class DateConverter {
    
    /**
     * 将Date转换为Long（时间戳）
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
    
    /**
     * 将Long（时间戳）转换为Date
     */
    @TypeConverter
    public static Date timestampToDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}
