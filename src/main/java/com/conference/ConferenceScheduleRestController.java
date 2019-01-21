package com.conference;

import com.conference.beans.Talk;
import com.conference.configurations.EventConfiguration;
import com.conference.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ConferenceScheduleRestController {
    @Autowired
    private EventConfiguration eventConfiguration;
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/")
    public Map<String, List<Talk>> hello() throws Exception {
        return scheduleService.createSchedule();
    }
}
