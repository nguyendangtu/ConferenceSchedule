package com.conference;

import com.conference.configurations.EventConfiguration;
import com.conference.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestControllerExample {
    @Autowired
    private EventConfiguration eventConfiguration;
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/")
    public String hello() throws Exception {
        scheduleService.createSchedule();
        return "Hello ";
    }
}
