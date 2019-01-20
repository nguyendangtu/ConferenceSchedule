package com.conference.beans;

import com.conference.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Talk implements Serializable {

    private String type;

    private String description;

    private List<String> tags;

    private String title;

    private ScheduleTime scheduleTime;

    private int dayIndex;

    private int trackIndex;

    @Override
    public String toString() {
        String disPlayTitle = (null != title && "" != title) ? " " + title : "";
        return DateUtil.toHHmmFromScheduleTime(scheduleTime) + disPlayTitle + " " + type;
    }
}
