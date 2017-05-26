package com.zbar.lib.log;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <h1>日期工具类</h1>
 * <p/>
 * <p>主要实现了日期的常用操作</p>
 * <p/>
 * Created by jihf on 2016/6/24 0024.
 */
public class DateTimeUtils {
  private static final String TAG = DateTimeUtils.class.getSimpleName();
  /**
   * yyyy-MM-dd HH:mm:ss字符串 (日期加时间)
   */
  public static final String DEFAULT_FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

  /**
   * yyyy-MM-dd字符串 (日期)
   */
  public static final String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";
  /**
   * yyyy-MM-dd字符串 (日期)
   */
  public static final String CUSTOM_FORMAT_DATE = "yyyyMMdd";

  /**
   * HH:mm:ss字符串 (时间)
   */
  public static final String DEFAULT_FORMAT_TIME = "HH:mm:ss";

  /**
   * yyyy-MM-dd类型的日期格式化（日期）
   */
  public static SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_FORMAT_DATE);
  /**
   * yyyy-MM-dd类型的日期格式化（日期）
   */
  public static SimpleDateFormat customDateFormat = new SimpleDateFormat(CUSTOM_FORMAT_DATE);
  /**
   * HH:mm:ss类型的日期格式化（时间）
   */
  public static SimpleDateFormat timeFormat = new SimpleDateFormat(DEFAULT_FORMAT_TIME);
  /**
   * yyyy-MM-dd HH:mm:ss类型的日期格式化（日期+时间）
   */
  public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DEFAULT_FORMAT_DATE_TIME);

  /**
   * 获取当前日期（yyyy-MM-dd）字符串，并格式化
   *
   * @return yyyy-MM-dd
   */
  public static String getCurrentDateFormat() {
    return getDateFormat(Calendar.getInstance().getTime(), dateFormat);
  }

  /**
   * 获取当前日期（yyyyMMdd）字符串，并格式化
   *
   * @return yyyyMMdd
   */
  public static String getCustomDateFormat() {
    return getDateFormat(Calendar.getInstance().getTime(), customDateFormat);
  }

  /**
   * 获取当前日期+时间（yyyy-MM-dd HH:mm:ss）字符串，并格式化
   *
   * @return yyyy-MM-dd HH:mm:ss
   */
  public static String getCurrentDateTimeFormat() {
    return getDateFormat(Calendar.getInstance().getTime(), dateTimeFormat);
  }

  /**
   * 将long时间转成日期（yyyy-MM-dd）字符串，并格式化
   *
   * @param time 时间源
   * @return yyyy-MM-dd
   */
  public static String getDateFromLong(long time) {
    return getDateFormat(new Date(time), dateFormat);
  }

  /**
   * 将"yyyy-MM-dd HH:mm:ss" 格式的字符串转成Date
   *
   * @param strDate 时间字符串
   * @return Date
   */
  private static Date getDateFromString(String strDate) {
    return getDateFormat(strDate, dateFormat);
  }

  private static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
  private static final String[] weekDay = { "一", "二", "三", "四", "五", "六","天" };

  /**
   * 将String字符串按照一定格式转成Date<br>
   * 注： SimpleDateFormat为空时，采用默认的yyyy-MM-dd HH:mm:ss格式
   *
   * @param strDate 时间字符串
   * @param simpleDateFormat SimpleDateFormat对象
   * @throws ParseException 日期格式转换出错
   */
  public static Date getDateFormat(String strDate, SimpleDateFormat simpleDateFormat) {
    if (null == simpleDateFormat) {
      simpleDateFormat = dateTimeFormat;
    }
    try {
      Date date = simpleDateFormat.parse(strDate);
      return date;
    } catch (ParseException e) {
      e.printStackTrace();
      Log.e("ParseException", "date ParseException");
    }
    return null;
  }

  /**
   * 将long时间转成日期+时间(yyyy-MM-dd HH:mm:ss)字符串，并格式化
   *
   * @param time 时间源
   * @return yyyy-MM-dd HH:mm:ss
   */
  public static String getDateTimmeFromLong(long time) {
    return getDateFormat(new Date(time), dateTimeFormat);
  }

  /**
   * 将date转成日期(yyyy-MM-dd)字符串，并格式化
   *
   * @param date 时间源
   * @return yyyy-MM-dd
   */
  public static String getDateFormat(Date date) {
    return getDateFormat(date, dateFormat);
  }

  /**
   * 将date转成日期+时间(yyyy-MM-dd HH:mm:ss)字符串，并格式化
   *
   * @param date 时间源
   * @return yyyy-MM-dd HH:mm:ss
   */
  public static String getDateTimeFromDate(Date date) {
    return getDateFormat(date, dateTimeFormat);
  }

  /**
   * 将年月日的int转成yyyy-MM-dd的字符串
   *
   * @param year 年
   * @param month 月 1-12
   * @param day 日
   * @return yyyy-MM-dd
   * 注：月表示Calendar的月，比实际小1
   */
  public static String getDateFormat(int year, int month, int day) {
    return getDateFormat(getDateFromInt(year, month, day), dateFormat);
  }

  /**
   * 根据指定的年月日时分秒，返回一个Date对象。
   *
   * @param year 年
   * @param month 月 0-11
   * @param day 日
   * @param hourOfDay 小时 0-23
   * @param minute 分 0-59
   * @param second 秒 0-59
   * @return 一个Date对象
   */
  public static Date getDate(int year, int month, int day, int hourOfDay, int minute, int second) {
    if (isDateRight(year, month, day) && isTimeRight(hourOfDay, minute, second)) {

      Calendar calendar = Calendar.getInstance();
      calendar.set(year, month - 1, day, hourOfDay, minute, second);
      return calendar.getTime();
    } else {
      return null;
    }
  }

  /**
   * 使用Calendar将年月日的int转成date
   *
   * @param year 年
   * @param month 月 1-12
   * @param day 日
   * @return yyyy-MM-dd
   * 注：月表示Calendar的月，比实际小1
   */
  public static Date getDateFromInt(int year, int month, int day) {
    if (isDateRight(year, month, day)) {

      Calendar mCalendar = Calendar.getInstance();
      mCalendar.set(year, month - 1, day);
      return mCalendar.getTime();
    } else {
      return null;
    }
  }

  /**
   * 判断年月日是否合法
   */
  private static boolean isDateRight(int year, int month, int day) {
    boolean flag = true;
    if (year < 1970 || month < 0 || month > 12 || day < 0) {
      Log.e("input error", "your input is error");
      flag = false;
    }
    if (isLeapYear(year)) {
      if (month == 2 && day > 29) {
        //闰年(366天，2月份29天)
        Log.e("day error", "the day which you input is error");
        flag = false;
      }
    } else {
      if (month == 2 && day > 28) {
        //闰年(365天，2月份28天)
        Log.e("day error", "the day which you input is error");
        flag = false;
      }
    }
    return flag;
  }

  /**
   * 判断时分秒是否合法
   */
  private static boolean isTimeRight(int hourOfDay, int minute, int second) {
    boolean flag = true;
    if (hourOfDay < 0 || hourOfDay > 24) {
      Log.e("hourOfDay error", "the hourOfDay which you input is error");
      flag = false;
    }
    if (minute < 0 || minute > 60) {
      Log.e("minute error", "the minute which you input is error");
      flag = false;
    }
    if (second < 0 || second > 60) {
      Log.e("minute error", "the minute which you input is error");
      flag = false;
    }
    return flag;
  }

  /**
   * 判断当前年份是否是闰年
   * <p/>
   * 提示：
   * 闰年的条件是符合下面二者之一：
   * (1)年份能被4整除，但不能被100整除；（%）
   * (2)整百的年数能被400整除。
   *
   * @return true(闰年) , false(平年)
   */
  public static boolean isLeapYear(int year) {
    if (year < 1970) {
      Log.e("year error", "the year which you input is error");
      return false;
    }
    if (((year % 4 == 0) && (year % 100 != 0)) || year % 400 == 0) {
      return true;
    }
    return false;
  }

  /**
   * 获取格式化时间的实际操作方法
   */
  public static String getDateFormat(Date date, SimpleDateFormat simpleDateFormat) {
    if (simpleDateFormat == null) {
      simpleDateFormat = new SimpleDateFormat(DEFAULT_FORMAT_DATE_TIME);
    }
    return simpleDateFormat.format(date);
  }

  /**
   * 求两个日期相差天数
   *
   * @param start 起始日期，格式yyyy-MM-dd
   * @param end 终止日期，格式yyyy-MM-dd
   * @return 两个日期相差天数
   */
  public static long getIntervalDays(String start, String end) {
    return ((java.sql.Date.valueOf(end)).getTime() - (java.sql.Date.valueOf(start)).getTime()) / (3600 * 24 * 1000);
  }

  /**
   * 求两个日期差值
   *
   * @param start 起始日期，long型
   * @param end 终止日期，long型
   * @return 两个日期相差天数
   */
  public static long getIntervalTime(long start, long end) {
    long diff = 0;
    //Date startTime = new Date(start);
    //Date endTime = new Date(end);
    diff = (start - end);
    return diff;
  }

  /**
   * 获得当前星期几
   *
   * @return day(String)
   */
  public static String getCurrentWeekeyDay() {
    int currentWayDay = 1;
    try {
      currentWayDay = calendar.get(Calendar.DAY_OF_WEEK);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return weekDay[currentWayDay];
  }

  /**
   * 获得当前年份
   *
   * @return year(int)
   */
  public static int getCurrentYear() {
    return calendar.get(Calendar.YEAR);
  }

  /**
   * 获得当前月份
   *
   * @return month(int) 1-12
   */
  public static int getCurrentMonth() {
    return calendar.get(Calendar.MONTH) + 1;
  }

  /**
   * 获得当月几号
   *
   * @return day(int)
   */
  public static int getCurrentDay() {
    return calendar.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 获得昨天的日期(格式：yyyy-MM-dd)
   *
   * @return yyyy-MM-dd
   */
  public static String getYesterday() {
    Calendar mCalendar = Calendar.getInstance();
    mCalendar.add(Calendar.DATE, -1);
    return getDateFormat(mCalendar.getTime());
  }

  /**
   * 获得前天的日期(格式：yyyy-MM-dd)
   *
   * @return yyyy-MM-dd
   */
  public static String getBeforeYesterday() {
    Calendar mCalendar = Calendar.getInstance();
    mCalendar.add(Calendar.DATE, -2);
    return getDateFormat(mCalendar.getTime());
  }

  /**
   * 获得几天之前或者几天之后的日期
   *
   * @param diff 差值：正的往后推，负的往前推
   */
  public static String getOtherDay(int diff) {
    Calendar mCalendar = Calendar.getInstance();
    mCalendar.add(Calendar.DATE, diff);
    return getDateFormat(mCalendar.getTime());
  }

  /**
   * 取得给定日期加上一定天数后的日期对象.
   *
   * @param strDate 给定的日期对象
   * @param amount 需要添加的天数，如果是向前的天数，使用负数就可以.
   * @return Date 加上一定天数以后的Date对象.
   */
  public static String getCalcDateFormat(String strDate, int amount) {
    Date date = getCalcDate(getDateFromString(strDate), amount);
    return getDateFormat(date);
  }

  /**
   * 取得给定日期加上一定天数后的日期对象.
   *
   * @param date 给定的日期对象
   * @param amount 需要添加的天数，如果是向前的天数，使用负数就可以.
   * @return Date 加上一定天数以后的Date对象.
   */
  public static Date getCalcDate(Date date, int amount) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, amount);
    return cal.getTime();
  }
}
