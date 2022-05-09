/*
 *      Copyright [2020] [xiaozhennan1995@gmail.com]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *      http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weiwan.dsp.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Date: 2018/8/23 11:14
 * @Author: xiaozhennan
 * @Package: com.ipaynow.dc.common.utils
 * @ClassName: DateUtils
 * @Description:
 **/
public class DateUtils {
    public static final String SDF_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String TRANS_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF_YYYY_MM_DD = "yyyyMMdd";
    public static final String SDF_YYYY_MM = "yyyyMM";
    public static final String RM_SDF_YYYY_MM_DD = "yyyy-MM-dd";


    private static final String TIME_ZONE = "GMT+8";

    private static final String STANDARD_DATETIME_FORMAT = "standardDatetimeFormatter";

    private static final String UN_STANDARD_DATETIME_FORMAT = "unStandardDatetimeFormatter";

    private static final String DATE_FORMAT = "dateFormatter";

    private static final String TIME_FORMAT = "timeFormatter";

    private static final String YEAR_FORMAT = "yearFormatter";

    private static final String START_TIME = "1970-01-01";

    public final static String DATE_REGEX = "(?i)date";

    public final static String TIMESTAMP_REGEX = "(?i)timestamp";

    public final static String DATETIME_REGEX = "(?i)datetime";

    public final static int LENGTH_SECOND = 10;
    public final static int LENGTH_MILLISECOND = 13;
    public final static int LENGTH_MICROSECOND = 16;
    public final static int LENGTH_NANOSECOND = 19;



    //30*24*60=432000 一个月的分钟数
    public static final Integer LIMITENDTIME = 43200;


    public static ThreadLocal<Map<String,SimpleDateFormat>> datetimeFormatter = ThreadLocal.withInitial(() -> {
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);

        Map<String, SimpleDateFormat> formatterMap = new HashMap<>();

        SimpleDateFormat standardDatetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        standardDatetimeFormatter.setTimeZone(timeZone);
        formatterMap.put(STANDARD_DATETIME_FORMAT,standardDatetimeFormatter);

        SimpleDateFormat unStandardDatetimeFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
        unStandardDatetimeFormatter.setTimeZone(timeZone);
        formatterMap.put(UN_STANDARD_DATETIME_FORMAT,unStandardDatetimeFormatter);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setTimeZone(timeZone);
        formatterMap.put(DATE_FORMAT,dateFormatter);

        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        timeFormatter.setTimeZone(timeZone);
        formatterMap.put(TIME_FORMAT,timeFormatter);

        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
        yearFormatter.setTimeZone(timeZone);
        formatterMap.put(YEAR_FORMAT,yearFormatter);

        return formatterMap;
    });

    public static java.sql.Date columnToDate(Object column,SimpleDateFormat customTimeFormat) {
        if(column == null) {
            return null;
        } else if(column instanceof String) {
            if (((String) column).length() == 0){
                return null;
            }
            return new java.sql.Date(stringToDate((String)column,customTimeFormat).getTime());
        } else if (column instanceof Integer) {
            Integer rawData = (Integer) column;
            return new java.sql.Date(getMillSecond(rawData.toString()));
        } else if (column instanceof Long) {
            Long rawData = (Long) column;
            return new java.sql.Date(getMillSecond(rawData.toString()));
        } else if (column instanceof java.sql.Date) {
            return (java.sql.Date) column;
        } else if(column instanceof Timestamp) {
            Timestamp ts = (Timestamp) column;
            return new java.sql.Date(ts.getTime());
        } else if(column instanceof Date) {
            Date d = (Date)column;
            return new java.sql.Date(d.getTime());
        }

        throw new IllegalArgumentException("Can't convert " + column.getClass().getName() + " to Date");
    }

    public static Timestamp columnToTimestamp(Object column,SimpleDateFormat customTimeFormat) {
        if (column == null) {
            return null;
        } else if(column instanceof String) {
            if (((String) column).length() == 0){
                return null;
            }
            return new Timestamp(stringToDate((String)column,customTimeFormat).getTime());
        } else if (column instanceof Integer) {
            Integer rawData = (Integer) column;
            return new Timestamp(getMillSecond(rawData.toString()));
        } else if (column instanceof Long) {
            Long rawData = (Long) column;
            return new Timestamp(getMillSecond(rawData.toString()));
        } else if (column instanceof java.sql.Date) {
            return new Timestamp(((java.sql.Date) column).getTime());
        } else if(column instanceof Timestamp) {
            return (Timestamp) column;
        } else if(column instanceof Date) {
            Date d = (Date)column;
            return new Timestamp(d.getTime());
        }

        throw new IllegalArgumentException("Can't convert " + column.getClass().getName() + " to Date");
    }

    public static long getMillSecond(String data){
        long time  = Long.parseLong(data);
        if(data.length() == LENGTH_SECOND){
            time = Long.parseLong(data) * 1000;
        } else if(data.length() == LENGTH_MILLISECOND){
            time = Long.parseLong(data);
        } else if(data.length() == LENGTH_MICROSECOND){
            time = Long.parseLong(data) / 1000;
        } else if(data.length() == LENGTH_NANOSECOND){
            time = Long.parseLong(data) / 1000000 ;
        } else if(data.length() < LENGTH_SECOND){
            try {
                long day = Long.parseLong(data);
                Date date = datetimeFormatter.get().get(DATE_FORMAT).parse(START_TIME);
                Calendar cal = Calendar.getInstance();
                long addMill = date.getTime() + day * 24 * 3600 * 1000;
                cal.setTimeInMillis(addMill);
                time = cal.getTimeInMillis();
            } catch (Exception ignore){
            }
        }
        return time;
    }

    public static Date stringToDate(String strDate,SimpleDateFormat customTimeFormat)  {
        if(strDate == null || strDate.trim().length() == 0) {
            return null;
        }

        if(customTimeFormat != null){
            try {
                return customTimeFormat.parse(strDate);
            } catch (ParseException ignored) {
            }
        }

        try {
            return datetimeFormatter.get().get(STANDARD_DATETIME_FORMAT).parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return datetimeFormatter.get().get(UN_STANDARD_DATETIME_FORMAT).parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return datetimeFormatter.get().get(DATE_FORMAT).parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return datetimeFormatter.get().get(TIME_FORMAT).parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return datetimeFormatter.get().get(YEAR_FORMAT).parse(strDate);
        } catch (ParseException ignored) {
        }

        throw new RuntimeException("can't parse date");
    }

    public static String dateToString(Date date) {
        return datetimeFormatter.get().get(DATE_FORMAT).format(date);
    }

    public static String timestampToString(Date date) {
        return datetimeFormatter.get().get(STANDARD_DATETIME_FORMAT).format(date);
    }

    public static String dateToYearString(Date date) {
        return datetimeFormatter.get().get(YEAR_FORMAT).format(date);
    }

    public static SimpleDateFormat getDateTimeFormatter(){
        return datetimeFormatter.get().get(STANDARD_DATETIME_FORMAT);
    }

    public static SimpleDateFormat getDateFormatter(){
        return datetimeFormatter.get().get(DATE_FORMAT);
    }

    public static SimpleDateFormat getTimeFormatter(){
        return datetimeFormatter.get().get(TIME_FORMAT);
    }

    public static SimpleDateFormat getYearFormatter(){
        return datetimeFormatter.get().get(YEAR_FORMAT);
    }

    public static SimpleDateFormat buildDateFormatter(String timeFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        sdf.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        return sdf;
    }

    public static String getNowDateStr() {
        return getDateStr(new Date());
    }

    public static String getDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SDF_YYYYMMDDHHMMSS);
        return dateFormat.format(date);
    }
    public static String getDateStrYMDHMS(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS);
        return dateFormat.format(date);
    }


    public static Date getTodayOriginDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
//        cal.add(Calendar.DAY_OF_MONTH, 1); //当前时间加1天
        return cal.getTime();
    }

    /**
     * 用于计算当前时间加上步长
     *
     * @param step 步长
     * @return
     */
    public static Date dateFormat(int step) {
        Date date;
        Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        cl.add(Calendar.MINUTE, step);
        date = cl.getTime();
        return date;
    }

    public static Date getTimeout(Date startTime, int step) {
        Date date;
        Calendar cl = Calendar.getInstance();
        cl.setTime(startTime);

        cl.add(Calendar.SECOND, step);
        date = cl.getTime();

        return date;

    }

    public static Date dateFormatForSecond(int step) {
        Date date;
        Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        cl.add(Calendar.SECOND, step);
        date = cl.getTime();
        return date;
    }

    public static int getCurrentBySeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static int getDateSeconds(Date date) {

        return (int) (date.getTime() / 1000);
    }

    /**
     * 获取d之前day的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    public static Date getMinuteBefore(Date d, int minute) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.add(Calendar.MINUTE, -minute);
        return now.getTime();
    }


    /**
     * 限制查询间隔，不超过30天。
     *
     * @param startTime
     * @return
     */
    public static Integer limitQueryTime(Integer startTime, Integer endTime) {
        //开始时间加上30天的时间间隔作为结束时间
        if (endTime - startTime > LIMITENDTIME) {
            endTime = startTime + LIMITENDTIME;
        }
        return endTime;
    }

    public static Date converStringToDate(String dateStr, String format) {

        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date formTransTime(String value) {

        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(TRANS_TIME);
            date = dateFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date formWXPayTime(String value) {

        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(SDF_YYYYMMDDHHMMSS);
            date = dateFormat.parse(value);
        } catch (ParseException e) {
//            e.printStackTrace();
        }
        return date;
    }

    public static Date formRMTime(String value) {

        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(RM_SDF_YYYY_MM_DD);
            date = dateFormat.parse(value);
        } catch (ParseException e) {
//            e.printStackTrace();
        }
        return date;
    }

    public static String fromDate2RM(Date value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RM_SDF_YYYY_MM_DD);
        String result = dateFormat.format(value);
        return result;
    }
    public static String fromDate2TransTime(Date value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TRANS_TIME);
        String result = dateFormat.format(value);
        return result;
    }

    public static String fromDate2Str(Date value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SDF_YYYYMMDDHHMMSS);
        String result = dateFormat.format(value);
        return result;
    }

    public static String convertTimeDate(Date value){
        SimpleDateFormat dateFormat = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS);
        return dateFormat.format(value);
    }

    public static String convertTimeStr(String value) {
        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(SDF_YYYYMMDDHHMMSS);
            date = dateFormat.parse(value);
        } catch (Exception e) {

        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(SDF_YYYY_MM_DD_HH_MM_SS);
        return dateFormat1.format(date);

    }

    public static String formatDateToMonthStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SDF_YYYY_MM);
        String result = dateFormat.format(date);
        return result;
    }

    public static int getIntervalDays(Date fDate, Date oDate) {

        Calendar aCalendar = Calendar.getInstance();

        aCalendar.setTime(fDate);

        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

        aCalendar.setTime(oDate);

        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

        return day2 - day1;

    }
    public static String dateDayFormatStr(int step) {
        Date date;
        Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        cl.add(Calendar.DAY_OF_MONTH, step);
        date = cl.getTime();
        return formatDateToDayStr(date);
    }

    public static String formatDateToDayStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SDF_YYYY_MM_DD);
        String result = dateFormat.format(date);
        return result;
    }


    //得到此刻时间，减去10分钟
    public static Date getTimeBefore60Minute(int minute, int num) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(minute, num);
        Date beforeTime10 = gc.getTime();
        return beforeTime10;
    }

    /**
     * 日期加减操作
     *
     * @param currentDate 当前日期
     * @return
     */
    public static Date getSpecifiedDateBeforeOrAfter(Date currentDate, int seconds) {
        Calendar c = Calendar.getInstance();
        int second = c.get(Calendar.SECOND);
        c.set(Calendar.SECOND, second + seconds);
        return c.getTime();
    }

    /**
     *
     * @param d
     * @param format
     * @param rtnFormat
     * @return
     */
    public static String getDateTimeFromString(String d, String format,String rtnFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        SimpleDateFormat rtnf = new SimpleDateFormat(rtnFormat);
        try {
            return rtnf.format(sdf.parse(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据指定日期格式验证日期
     * @param date
     * @param dateFormat
     * @return
     */
    public static boolean isValidDate(String date, String dateFormat) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(date);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if(date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if(cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }





    public static void main(String[] args) {
        String nowDateStr = getDateStr(formTransTime("2018-12-21 10:15:58"));
        System.out.println(nowDateStr);
    }
}
