package org.simple.base.util

import groovy.transform.CompileStatic
import org.apache.commons.lang3.time.DateUtils

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * @author zhangjp 2015-04-28
 */
@CompileStatic
class DateUtil extends DateUtils {
    public final static String DATE_FORMAT_DATE = 'yyyy-MM-dd'
    public final static String DATE_FORMAT_TIME = 'yyyy-MM-dd HH:mm:ss'

    /**
     * 用yyyy-MM-dd HH:mm:ss格式化日期
     *
     * @param date
     * @return
     */
    static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_TIME)

        return sdf.format(date)
    }

    /**
     * 用pattern格式化日期
     *
     * @param date 日期
     * @param pattern 格式
     * @return
     * @throws ParseException
     */
    static  String formatDate(Date date, String pattern) throws ParseException {
        if(StringUtil.isNullOrEmpty(pattern)) {
            pattern = DATE_FORMAT_TIME
        }
        SimpleDateFormat sdf
        try {
            sdf = new SimpleDateFormat(pattern)

            return sdf.format(date)
        } catch (ParseException pe) {
            throw new ParseException("Unable to format the date with pattern: " + pattern, -1)
        }
    }

    /**
     * 计算两个日期之间相隔的天数
     * @param start
     * @param end
     * @return
     */
    static int getDaysBetween(Date start, Date end) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DATE)
        start = sdf.parse(sdf.format(start))
        end = sdf.parse(sdf.format(end))
        Calendar cal = Calendar.getInstance()
        cal.setTime(start)
        long time1 = cal.getTimeInMillis()
        cal.setTime(end)
        long time2 = cal.getTimeInMillis()
        def between_days = (time2 - time1) / (1000 * 3600 * 24)
        return Integer.parseInt(String.valueOf(between_days))
    }

    /**
     * 返回一天的开始时间
     *
     * @param day
     * @return
     */
    static Date getDayEnd(Date day) {
        Calendar dc = Calendar.getInstance();
        dc.setTime(day)
        dc.set(Calendar.HOUR_OF_DAY, 23);
        dc.set(Calendar.MINUTE, 59);
        dc.set(Calendar.SECOND, 59)
        dc.set(Calendar.MILLISECOND, 0)
        return dc.getTime()
    }

    /**
     * 返回一天的结束时间
     *
     * @param day
     * @return
     */
    static Date getDayStart(Date day) {
        Calendar dc = Calendar.getInstance();
        dc.setTime(day)
        dc.set(Calendar.HOUR_OF_DAY, 00);
        dc.set(Calendar.MINUTE, 00);
        dc.set(Calendar.SECOND, 00)
        dc.set(Calendar.MILLISECOND, 0)
        return dc.getTime()
    }
}
