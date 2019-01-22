package com.conference.service;

import com.conference.beans.Talk;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/***
 * ScheduleService is a service which allowing client use it to create a schedule.
 */
public interface ScheduleService {
    /**
     * Create a  base schedule, base schedule only contain all fix talk for each conference
     *
     * @return
     * @throws IOException
     */
    List<Talk> createBaseSchedule() throws IOException;

    /**
     * Create a schedule, a schedule will contain all fix schedule and all dynamic schedule
     *
     * @return
     * @throws IOException
     */
    Map<String, List<Talk>> createSchedule() throws IOException;
}
