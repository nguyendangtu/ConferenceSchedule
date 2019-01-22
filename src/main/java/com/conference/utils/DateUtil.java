package com.conference.utils;

import com.conference.beans.ScheduleTime;
import com.conference.constants.Constant;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * DateUtil is a until for date which provide api to handle date time in this project
 */
public class DateUtil {

    /**
     * convert from local time to string with format HH:mm
     *
     * @param localTime
     * @return
     */
    public static String toHHmm(LocalTime localTime) {
        if (null != localTime) {
            return localTime.format(DateTimeFormatter.ofPattern(Constant.HH_MM));
        }
        return "";
    }

    /**
     * Convert Schedule Time to string with format HH:mm
     *
     * @param scheduleTime
     * @return
     */
    public static String toHHmmFromScheduleTime(ScheduleTime scheduleTime) {
        if (null != scheduleTime) {
            return toHHmm(scheduleTime.getStartTime());
        }
        return "";
    }

    /**
     * Get duration from startTime and endTime
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getDuration(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        Duration duration = Duration.between(start, end);
        return duration.toMinutes();
    }
}
