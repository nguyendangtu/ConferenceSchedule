package com.conference.service;

import com.conference.beans.Talk;

import java.io.IOException;
import java.util.List;

public interface ScheduleService {
    List<Talk> createBaseSchedule() throws IOException;

    void createSchedule() throws IOException;
}
