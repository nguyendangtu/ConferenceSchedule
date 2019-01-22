package com.conference.utils;

import com.conference.beans.Talks;
import com.conference.constants.Constant;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ScheduleUtilTest {

    @Test
    public void getTalks() throws IOException {
        Talks talks = ScheduleUtil.getTalks(Constant.JSON_SRC_FILE_PATH);
        Assert.assertNotNull(talks);
        Assert.assertTrue(talks.size() > 0);
        talks.getValue().forEach(System.out::println);
    }


}