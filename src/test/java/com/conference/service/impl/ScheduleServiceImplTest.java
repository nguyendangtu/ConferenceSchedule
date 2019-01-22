package com.conference.service.impl;

import com.conference.BaseTest;
import com.conference.beans.Talk;
import com.conference.configurations.EventConfiguration;
import com.conference.constants.Constant;
import com.conference.service.ScheduleService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ScheduleServiceImplTest extends BaseTest {

    @Autowired
    private EventConfiguration eventConfiguration;

    @Autowired
    private ScheduleService scheduleService;


    @Test
    public void createSchedule() throws IOException {
        scheduleService.createSchedule();
    }

    @Test
    public void createBaseSchedule() {
    }


    @Test
    public void getNumberOfDayByClosing() {
        ScheduleServiceImpl scheduleService1 = new ScheduleServiceImpl();
        Talk talk1 = new Talk();
        Talk talk2 = new Talk();
        talk1.setType(Constant.TALK_TYPE.CLOSING.name());
        talk2.setType(Constant.TALK_TYPE.CLOSING.name());
        List<Talk> talks = new ArrayList<>();
        talks.add(talk1);
        talks.add(talk2);
        Long numberOfDay = (Long) invokdePrivateMethod(scheduleService1, "getNumberOfDayByClosing", talks);
        Assert.assertEquals(new Long("2"), numberOfDay);
    }
}