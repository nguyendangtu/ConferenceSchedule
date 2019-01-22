package com.conference.utils;

import com.conference.BaseTest;
import com.conference.beans.Talk;
import com.conference.beans.Talks;
import com.conference.configurations.EventConfiguration;
import com.conference.constants.Constant;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ScheduleUtilTest extends BaseTest {

    @Autowired
    private EventConfiguration eventConfiguration;

    @Test
    public void getTalks() throws IOException {
        Talks talks = ScheduleUtil.getTalks(Constant.JSON_SRC_FILE_PATH);
        Assert.assertNotNull(talks);
        Assert.assertTrue(talks.size() > 0);
        talks.getValue().forEach(System.out::println);
    }

    @Test
    public void getTea() {
        Talk talk = ScheduleUtil.getTea(eventConfiguration);
        Assert.assertNotNull(talk);
        Assert.assertEquals(Constant.TALK_TYPE.TEA.name(), talk.getType());
        Assert.assertEquals("15:00", DateUtil.toHHmm(talk.getScheduleTime().getStartTime()));
    }

    @Test
    public void getLunch() {
        Talk talk = ScheduleUtil.getLunch(eventConfiguration);
        Assert.assertNotNull(talk);
        Assert.assertEquals(Constant.TALK_TYPE.LUNCH.name(), talk.getType());
        Assert.assertEquals("12:30", DateUtil.toHHmm(talk.getScheduleTime().getStartTime()));
    }
}