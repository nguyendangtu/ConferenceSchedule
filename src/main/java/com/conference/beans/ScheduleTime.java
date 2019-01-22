package com.conference.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/***
 * Schedule time is using for store time for an event which have start time and duration
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleTime {

    private LocalTime startTime;

    private LocalTime endTime;

    /***
     * duration is the talking time in minute
     */
    private long duration;

}
