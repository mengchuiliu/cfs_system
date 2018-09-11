package com.xxjr.cfs_system.tools;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chuiliu Meng on 2016/8/9.
 * 日期处理类
 *
 * @author Chuiliu Meng
 */
@SuppressLint("ALL")
public class DateUtil {
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 将 2000-1-1类型数据转换为long时间戳
     */
    public static long getDateLong(String str) {
        long dLong = 0L;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
            Date date = dateFormat.parse(str);
            dLong = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dLong;
    }

    /**
     * 将 2000-1-1类型数据转换为long时间戳
     */
    public static long getTimeLong(String str) {
        long dLong = 0L;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(str);
            dLong = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dLong;
    }

    /**
     * 将pattern类型数据转换为long时间戳
     *
     * @param curDate 传入需要转换的时间
     * @param pattern 传入的时间格式
     */
    public static long getTimeLong(String curDate, String pattern) {
        long dLong = 0L;
        if (!TextUtils.isEmpty(curDate)) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                Date date = dateFormat.parse(curDate);
                dLong = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dLong;
    }

    /**
     * 将 2000-1-1类型数据转换为2001-01-01
     */
    public static String FormatTime(String str) {
        String time = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
            Date date = dateFormat.parse(str);
            time = getFormatDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getFormatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getFormatDateHH(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    /**
     * 计算某年某周的开始日期
     *
     * @param yearNum 格式 yyyy ，必须大于1900年度 小于9999年
     * @param weekNum 1到52或者53
     * @return 日期，格式为yyyy-MM-dd
     */
    public static String getYearWeekFirstDay(int yearNum, int weekNum) {
        if (yearNum < 1900 || yearNum > 9999) {
            throw new NullPointerException("年度必须大于等于1900年小于等于9999年");
        }
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
        // 上面两句代码配合，才能实现，每年度的第一个周，是包含第一个星期一的那个周。
        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        cal.set(Calendar.YEAR, yearNum);
        cal.set(Calendar.WEEK_OF_YEAR, weekNum);
        // 分别取得当前日期的年、月、日
        return getFormatDate(cal.getTime());
    }

    /**
     * 计算某年某周的结束日期
     *
     * @param yearNum 格式 yyyy ，必须大于1900年度 小于9999年
     * @param weekNum 1到52或者53
     * @return 日期，格式为yyyy-MM-dd
     */
    public static String getYearWeekEndDay(int yearNum, int weekNum) {
        if (yearNum < 1900 || yearNum > 9999) {
            throw new NullPointerException("年度必须大于等于1900年小于等于9999年");
        }
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 每周从周一开始
        // 上面两句代码配合，才能实现，每年度的第一个周，是包含第一个星期一的那个周。
        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        cal.set(Calendar.YEAR, yearNum);
        cal.set(Calendar.WEEK_OF_YEAR, weekNum);
        return getFormatDate(cal.getTime());
    }

    /**
     * 计算指定年度共有多少个周。
     *
     * @ param year 格式 yyyy ，必须大于1900年度 小于9999年
     * @ return
     */
    public static int getWeekNumByYear(final int year) {
        if (year < 1900 || year > 9999) {
            throw new NullPointerException("年度必须大于等于1900年小于等于9999年");
        }
        int result = 52;// 每年至少有52个周 ，最多有53个周。
        String date = getYearWeekFirstDay(year, 53);
        if (date.substring(0, 4).equals(year + "")) { // 判断年度是否相符，如果相符说明有53个周。
            result = 53;
        }
        return result;
    }

    /**
     * 星期几
     *
     * @param time long 系统时间的long类型
     * @return 星期一到星期日
     */
    public static String getWeekOfDate(long time) {
        Date date = new Date(time);
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    //获取当前年月
    public static String getDiaryDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM");
        return format.format(date);
    }

    //获取当前日
    public static String getCurDay(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    //获取当前日期
    public static String getCurDateDouble(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    //获取当前日期
    public static String getCurDate(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    //获取当前日期
    public static String getCurDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }

    public static String getChooseDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        return format.format(date);
    }

    public static String formatChooseDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    /**
     * 返回时分 hh:mm
     *
     * @param time long系统时间
     * @return String 例如19:39
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @param curDate     需要提前的日期
     * @return
     */
    public static String getOldDate(int distanceDay, String curDate) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = null;
        try {
            Date beginDate = dft.parse(curDate);
            Calendar date = Calendar.getInstance();
            date.setTime(beginDate);
            date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Logger.e("前N天== %s", dft.format(endDate));
        return dft.format(endDate);
    }

    //获取某个日期的天数
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    //获取选择月份的最大天数
    public static int getDayOfMonth(String chooseMonth) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Calendar calendar = Calendar.getInstance();//得到日历
        try {
            calendar.setTime(dft.parse(chooseMonth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param space 时间间隔月份
     * @return 返回当前日期前几个月时间
     */
    public static String getBeforeMonth(int space) {
        SimpleDateFormat sdf = new SimpleDateFormat("M");
        Calendar cur = Calendar.getInstance();
        cur.setTime(new Date());
        cur.add(Calendar.MONTH, space);
        return sdf.format(cur.getTime());
    }
}
