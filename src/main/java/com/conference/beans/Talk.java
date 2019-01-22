package com.conference.beans;

import com.conference.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/***
 * Talk is a main bean of a conference schedule, it will store a information for one meet up session.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Talk implements Serializable {

    private String type;

    private String description;

    private List<String> tags;

    private String title;

    private ScheduleTime scheduleTime;

    @Override
    public String toString() {
        String disPlayTitle = (null != title && "" != title) ? " " + title : "";
        return DateUtil.toHHmmFromScheduleTime(scheduleTime) + disPlayTitle + " " + type;
    }
}
