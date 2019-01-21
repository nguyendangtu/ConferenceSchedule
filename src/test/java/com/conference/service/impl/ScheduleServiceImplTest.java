package com.conference.service.impl;

import com.conference.BaseTest;
import com.conference.configurations.EventConfiguration;
import com.conference.service.ScheduleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

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
}