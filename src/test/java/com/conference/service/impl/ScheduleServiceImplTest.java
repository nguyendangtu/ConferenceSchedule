package com.conference.service.impl;

import com.conference.BaseTest;
import com.conference.beans.GroupTalk;
import com.conference.beans.ScheduleTime;
import com.conference.beans.Talk;
import com.conference.configurations.EventConfiguration;
import com.conference.constants.Constant;
import com.conference.service.ScheduleService;
import com.conference.utils.DateUtil;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ScheduleServiceImplTest extends BaseTest {

    @Autowired
    private EventConfiguration eventConfiguration;

    @Autowired
    private ScheduleService scheduleService;

    private EnumSet<Constant.TALK_TYPE> STATIC_TALK_TYPE = EnumSet.of(Constant.TALK_TYPE.KEYNOTE,
            Constant.TALK_TYPE.LUNCH,
            Constant.TALK_TYPE.TEA,
            Constant.TALK_TYPE.CLOSING);

    private EnumSet<Constant.TALK_TYPE> DYNAMIC_TALK_TYPE = EnumSet.of(Constant.TALK_TYPE.WORKSHOP,
            Constant.TALK_TYPE.REGULAR_TALK,
            Constant.TALK_TYPE.LIGHTNING,
            Constant.TALK_TYPE.PANEL_DISCUSSION);

    @Test
    public void createSchedule() throws IOException {
        scheduleService.createSchedule();
    }

    @Test
    public void createBaseSchedule() {
    }

    @Test
    public void createFinalSchedule() {

    }

    @Test
    public void addTalksToGroupTalks_Group1() throws IOException {
        List<Talk> baseSchedule = scheduleService.createBaseSchedule();
        List<Talk> dynamicSchedule = baseSchedule.parallelStream()
                .filter(slot -> DYNAMIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(slot.getType())))
                .collect(Collectors.toList());

        List<GroupTalk> groupTalks = new ArrayList<>();
        GroupTalk groupTalk = new GroupTalk();
        groupTalk.setStartTime(LocalTime.parse("09:30"));
        groupTalk.setAvailableTimes(180);
        groupTalk.setTotalTimes(180);
        groupTalks.add(groupTalk);
        Object[] args = new Object[]{dynamicSchedule, groupTalks};
        invokdePrivateMethod(scheduleService, "addTalksToGroupTalks", args);
        Assert.assertEquals(3, groupTalk.getTalks().size());
        long[] sumTalkDuration = new long[]{0};
        groupTalk.getTalks().forEach(talk -> sumTalkDuration[0] += talk.getScheduleTime().getDuration());
        Assert.assertEquals(180, sumTalkDuration[0]);
    }

    @Test
    public void addTalksToGroupTalks_Group3() throws IOException {
        List<Talk> baseSchedule = scheduleService.createBaseSchedule();
        List<Talk> dynamicSchedule = baseSchedule.parallelStream()
                .filter(slot -> DYNAMIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(slot.getType())))
                .collect(Collectors.toList());

        List<GroupTalk> groupTalks = new ArrayList<>();
        GroupTalk groupTalk = new GroupTalk();
        groupTalk.setStartTime(LocalTime.parse("15:15"));
        groupTalk.setAvailableTimes(105);
        groupTalk.setTotalTimes(105);
        groupTalks.add(groupTalk);
        Object[] args = new Object[]{dynamicSchedule, groupTalks};
        invokdePrivateMethod(scheduleService, "addTalksToGroupTalks", args);
        Assert.assertEquals(3, groupTalk.getTalks().size());
        long[] sumTalkDuration = new long[]{0};
        groupTalk.getTalks().forEach(talk -> sumTalkDuration[0] += talk.getScheduleTime().getDuration());
        Assert.assertEquals(100, sumTalkDuration[0]);
    }

    @Test
    public void createGroupTalks() throws IOException {
        List<Talk> baseSchedule = scheduleService.createBaseSchedule();
        List<Talk> fixSchedule = baseSchedule.parallelStream()
                .filter(slot -> STATIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(slot.getType())))
                .collect(Collectors.toList());
        Object[] args = new Object[]{fixSchedule};
        List<GroupTalk> groupTalks = (List) invokdePrivateMethod(scheduleService, "createGroupTalks", args);
        Assert.assertNotNull(groupTalks);
        Assert.assertEquals(3 * 2, groupTalks.size());
    }

    @Test
    public void createBaseGroupTalk() throws IOException {
        List<Talk> baseSchedule = scheduleService.createBaseSchedule();
        List<Talk> fixSchedule = baseSchedule.parallelStream()
                .filter(slot -> STATIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(slot.getType())))
                .collect(Collectors.toList());
        Object[] args = new Object[]{fixSchedule};
        List<GroupTalk> groupTalks = (List) invokdePrivateMethod(scheduleService, "createBaseGroupTalk", args);
        Assert.assertNotNull(groupTalks);
        Assert.assertEquals(4 * 2, groupTalks.size());
        groupTalks.forEach(i -> Assert.assertEquals(1, i.getTalks().size()));
    }

    @Test
    public void createTimeBand() throws IOException {
        List<Talk> baseSchedule = scheduleService.createBaseSchedule();
        List<Talk> fixSchedule = baseSchedule.parallelStream()
                .filter(slot -> STATIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(slot.getType())))
                .collect(Collectors.toList());
        Object[] args = new Object[]{fixSchedule};
        List<ScheduleTime> scheduleTimes = (List) invokdePrivateMethod(scheduleService, "createTimeBand", args);
        Assert.assertNotNull(scheduleTimes);
        Assert.assertEquals(4, scheduleTimes.size());
        for (int i = 0; i < scheduleTimes.size() - 1; i++) {
            Assert.assertTrue(scheduleTimes.get(i).getEndTime().isBefore(scheduleTimes.get(i + 1).getStartTime()));
        }
    }

    @Test
    public void setScheduleTime_KEYNOTE() {
        Object[] args = new Object[]{eventConfiguration, Constant.TALK_TYPE.KEYNOTE.name()};
        ScheduleTime scheduleTime = (ScheduleTime) invokdePrivateMethod(scheduleService, "setScheduleTime", args);
        Assert.assertNotNull(scheduleTime);
        Assert.assertEquals(DateUtil.toHHmm(scheduleTime.getStartTime()), eventConfiguration.getFirstTalkTime());
        Assert.assertEquals(DateUtil.toHHmm(scheduleTime.getEndTime()), "09:30");
        Assert.assertEquals(scheduleTime.getDuration(), 30);
    }

    @Test
    public void setScheduleTime_CLOSING() {
        Object[] args = new Object[]{eventConfiguration, Constant.TALK_TYPE.CLOSING.name()};
        ScheduleTime scheduleTime = (ScheduleTime) invokdePrivateMethod(scheduleService, "setScheduleTime", args);
        Assert.assertNotNull(scheduleTime);
        Assert.assertEquals(DateUtil.toHHmm(scheduleTime.getStartTime()), eventConfiguration.getLastTalkTime());
        Assert.assertEquals(DateUtil.toHHmm(scheduleTime.getEndTime()), "17:30");
        Assert.assertEquals(scheduleTime.getDuration(), 30);
    }

    @Test
    public void setScheduleTime_NORMAL() {
        Object[] args = new Object[]{eventConfiguration, Constant.TALK_TYPE.REGULAR_TALK.name()};
        ScheduleTime scheduleTime = (ScheduleTime) invokdePrivateMethod(scheduleService, "setScheduleTime", args);
        Assert.assertNotNull(scheduleTime);
        Assert.assertNull(scheduleTime.getStartTime());
        Assert.assertNull(scheduleTime.getEndTime());
        Assert.assertEquals(scheduleTime.getDuration(), 30);
    }

    @Test
    public void addLunchAndTeaToSchedule() {
        Talk talk1 = new Talk();
        Talk talk2 = new Talk();
        talk1.setType(Constant.TALK_TYPE.CLOSING.name());
        talk2.setType(Constant.TALK_TYPE.CLOSING.name());
        List<Talk> talks = new ArrayList<>();
        talks.add(talk1);
        talks.add(talk2);
        Object[] args = new Object[]{eventConfiguration, talks};
        invokdePrivateMethod(scheduleService, "addLunchAndTeaToSchedule", args);
        Assert.assertEquals(6, talks.size());
        int[] countTea = new int[]{0};
        int[] countLunch = new int[]{0};
        talks.forEach(i -> {
            if (i.getType().equalsIgnoreCase(Constant.TALK_TYPE.TEA.name())) {
                countTea[0]++;
            } else if (i.getType().equalsIgnoreCase(Constant.TALK_TYPE.LUNCH.name())) {
                countLunch[0]++;
            }
        });
        Assert.assertEquals(2, countLunch[0]);
        Assert.assertEquals(2, countTea[0]);

    }

    @Test
    public void getNumberOfDayByClosing() {
        Talk talk1 = new Talk();
        Talk talk2 = new Talk();
        talk1.setType(Constant.TALK_TYPE.CLOSING.name());
        talk2.setType(Constant.TALK_TYPE.CLOSING.name());
        List<Talk> talks = new ArrayList<>();
        talks.add(talk1);
        talks.add(talk2);
        Long numberOfDay = (Long) invokdePrivateMethod(scheduleService, "getNumberOfDayByClosing", new Object[]{talks});
        Assert.assertEquals(new Long("2"), numberOfDay);
    }

}