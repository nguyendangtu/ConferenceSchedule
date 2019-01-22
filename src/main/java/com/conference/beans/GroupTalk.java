package com.conference.beans;

import com.conference.utils.DateUtil;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/***
 * Schedule will have schedule constrain, group talk will group all talks in a range available constrain times.
 * @Data provide us getter/setter/ToString/EqualsAndHashCode/RequiredArgsConstructor for @GroupTalk
 */
@Data
public class GroupTalk {
    /**
     * start time is the time start for a group talk
     */
    private LocalTime startTime;

    /**
     * total time is total time for a group talk from beginning to the end
     */
    private long totalTimes;

    /**
     * available time is the time available after we add talk to group, available time will decrease when we add talk
     * to group talk, it will decrease util zero
     */
    private long availableTimes;

    /**
     * talk will contains all talk in group talk
     */
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
