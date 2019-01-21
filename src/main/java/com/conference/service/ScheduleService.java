package com.conference.service;

import com.conference.beans.Talk;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    List<Talk> createBaseSchedule() throws IOException;

    Map<String,List<Talk>> createSchedule() throws IOException;
}
