package com.conference.beans;

import com.conference.utils.DateUtil;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GroupTalk {
    private LocalTime startTime;
    private long totalTimes;
    private long availableTimes;
    private List<Talk> talks = new ArrayList<>();

    @Override
    public String toString() {
        String talkshow = "";
        for (Talk talk : talks) {
            talkshow = talkshow + ";" + talk.toString();
        }
        return "startTime=" + DateUtil.toHHmm(startTime) + ",totalTimes=" + totalTimes + ",availableTimes=" + availableTimes + ",talks=" + talkshow;
    }

}
