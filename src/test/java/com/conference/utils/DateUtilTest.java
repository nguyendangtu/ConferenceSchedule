package com.conference.utils;

import com.conference.beans.ScheduleTime;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class DateUtilTest {

    @Test
    public void toHHmm() {
        String startTime = "09:30";
        String expectedResult = "09:30";
        assertEquals(expectedResult, DateUtil.toHHmm(LocalTime.parse(startTime)));
    }

    @Test
    public void toHHmmFromScheduleTime() {
        String startTime = "09:30";
        String expectedResult = "09:30";
        ScheduleTime scheduleTime = new ScheduleTime();
        scheduleTime.setStartTime(LocalTime.parse(startTime));
        assertEquals(expectedResult, DateUtil.toHHmmFromScheduleTime(scheduleTime));
    }

    @Test
    public void getDuration() {
        String startTime = "09:00";
        String endTime = "09:10";
        assertEquals(10, DateUtil.getDuration(startTime, endTime));
    }

}