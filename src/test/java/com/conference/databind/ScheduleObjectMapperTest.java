package com.conference.databind;

import com.conference.beans.Talks;
import com.conference.constants.Constant;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ScheduleObjectMapperTest {

    @Test
    public void readValue() throws IOException {
        ScheduleObjectMapper scheduleObjectMapper = new ScheduleObjectMapper();
        Talks talks = scheduleObjectMapper.readValue(Constant.JSON_SRC_FILE_PATH, new TypeReference<Talks>() {
        });
        Assert.assertNotNull(talks);
        Assert.assertTrue(talks.size() > 0);
    }
}