package com.conference.utils;

import com.conference.beans.ScheduleTime;
import com.conference.constants.Constant;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String toHHmm(LocalTime localTime) {
        if (null != localTime) {
            return localTime.format(DateTimeFormatter.ofPattern(Constant.HH_MM));
        }
        return "";
    }

    public static String toHHmmFromScheduleTime(ScheduleTime scheduleTime) {
        if (null != scheduleTime) {
            return toHHmm(scheduleTime.getStartTime());
        }
        return "";
    }

    public static long getDuration(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        Duration duration = Duration.between(start, end);
        return duration.toMinutes();
    }
}
