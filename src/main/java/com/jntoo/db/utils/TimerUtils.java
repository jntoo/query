package com.jntoo.db.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimerUtils {
    /**
     * 格式化日期
     * @param format 格式化
     * @return 格式化后得日期
     */
    public static String date(String format) {
        return date(format, null);
    }
    /**
     * 根据时间戳格式化日期
     * @param format 格式化
     * @param time  时间戳 秒
     * @return 格式化后得日期
     */
    public static String date(String format, long time) {
        return date(format, new Date(time * 1000));
    }

    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
    public static long time() {
        Double aDouble = Double.valueOf(new Date().getTime() / 1000);
        return aDouble.longValue();
    }

    /**
     * 根据date 类型格式化日期
     * @param format 格式化
     * @param time 时间
     * @return 格式化后得日期
     */
    public static String date(String format, Date time) {
        if (time == null) {
            time = new Date();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(time);
    }

    /**
     * 获取当前日期时间
     * @return 当前日期
     */
    public static String getDateStr() {
        return date("yyyy-MM-dd HH:mm:ss");
    }



    /**
     * 获取某日期的上个月开始日期
     * @param currentDate 日期
     * @param format  日期格式
     * @return 上月开始日期
     */
    public static Date getPrevMonthStartDate(String currentDate , String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(currentDate));
            c.add(Calendar.MONTH, -1);
            //设置为1号,当前日期既为本月第一天
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 获取某日期的上个月结束日期
     * @param date 日期
     * @param format 日期格式
     * @return 上月结束日期
     */
    public static Date getPrevMonthEndDate(String date , String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
            c.add(Calendar.MONTH , -1);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 获取某日期的月开始日期
     * @param currentDate 日期
     * @param format 日期格式
     * @return 月开始日期
     */
    public static Date getMonthStartDate(String currentDate , String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(currentDate));
            c.add(Calendar.MONTH, 0);
            //设置为1号,当前日期既为本月第一天
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 获取某日期的月结束日期
     * @param date 日期
     * @param format 日期格式
     * @return 月结束日期
     */
    public static Date getMonthEndDate(String date , String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 判断时间戳是否是周末
     * @param bDate 日期
     * @return 是否
     */
    public static boolean isWeekend(Date bDate)  {
        return isWeekend(bDate.getTime() / 1000);
    }

    /**
     * 判断时间戳是否是周末
     * @param bDate 时间戳
     * @return 是否
     */
    public static boolean isWeekend(long bDate)  {
        //DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date bdate = new Date(bDate*1000); //format1.parse(bDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bdate);
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            return true;
        } else{
            return false;
        }
    }

}
