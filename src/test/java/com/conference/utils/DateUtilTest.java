package com.conference.utils;

import com.conference.beans.ScheduleTime;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;

public class DateUtilTest {

    @Test
    public void toHHmm() {
        String startTime = "09:30:00";
        String expectedResult = "09:30";
        assertEquals(expectedResult, DateUtil.toHHmm(LocalTime.parse(startTime)));
    }

    @Test
    public void toHHmmFromScheduleTime() {
        String startTime = "09:30:00";
        String expectedResult = "09:30";
        ScheduleTime scheduleTime = new ScheduleTime();
        scheduleTime.setStartTime(LocalTime.parse(startTime));
        assertEquals(expectedResult, scheduleTime);
    }

    @Test
    public void getDuration() {
        String startTime = "09:00";
        String endTime = "09:10";
        assertEquals(10, DateUtil.getDuration(startTime, endTime));
    }

    @Test
    public void doA(){
        //Collections.sort();
    }
}