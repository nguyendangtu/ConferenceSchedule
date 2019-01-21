package com.conference.utils;

import com.conference.beans.ScheduleTime;
import com.conference.beans.Talk;
import com.conference.beans.Talks;
import com.conference.configurations.EventConfiguration;
import com.conference.constants.Constant;
import com.conference.databind.ScheduleObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleUtil {

    public static Talks getTalks(String src) throws IOException {
        ScheduleObjectMapper objectMapper = new ScheduleObjectMapper();
        return objectMapper.readValue(src, new TypeReference<Talks>() {
        });
    }

    public static Talk getTea(EventConfiguration eventConfiguration) {
        LocalTime startTime = LocalTime.parse(eventConfiguration.getTea().getStartTime());
        LocalTime endTime = LocalTime.parse(eventConfiguration.getTea().getEndTime());
        long duration = eventConfiguration.getTea().getDuration();
        ScheduleTime scheduleTime = new ScheduleTime(startTime, endTime, duration);
        Talk tea = new Talk();
        tea.setType(Constant.TALK_TYPE.TEA.name());
        tea.setScheduleTime(scheduleTime);
        return tea;
    }

    public static Talk getLunch(EventConfiguration eventConfiguration) {
        LocalTime startTime = LocalTime.parse(eventConfiguration.getLunch().getStartTime());
        LocalTime endTime = LocalTime.parse(eventConfiguration.getLunch().getEndTime());
        long duration = eventConfiguration.getLunch().getDuration();
        ScheduleTime scheduleTime = new ScheduleTime(startTime, endTime, duration);
        Talk lunch = new Talk();
        lunch.setType(Constant.TALK_TYPE.LUNCH.name());
        lunch.setScheduleTime(scheduleTime);
        return lunch;
    }


}