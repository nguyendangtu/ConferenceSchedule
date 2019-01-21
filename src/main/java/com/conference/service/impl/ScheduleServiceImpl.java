package com.conference.service.impl;

import com.conference.beans.GroupTalk;
import com.conference.beans.ScheduleTime;
import com.conference.beans.Talk;
import com.conference.configurations.EventConfiguration;
import com.conference.constants.Constant;
import com.conference.service.ScheduleService;
import com.conference.utils.ScheduleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private EventConfiguration eventConfiguration;

    private EnumSet<Constant.TALK_TYPE> STATIC_TALK_TYPE = EnumSet.of(Constant.TALK_TYPE.KEYNOTE,
            Constant.TALK_TYPE.LUNCH, Constant.TALK_TYPE.TEA, Constant.TALK_TYPE.CLOSING);
    private EnumSet<Constant.TALK_TYPE> DYNAMIC_TALK_TYPE = EnumSet.of(Constant.TALK_TYPE.WORKSHOP,
            Constant.TALK_TYPE.REGULAR_TALK, Constant.TALK_TYPE.LIGHTNING, Constant.TALK_TYPE.PANEL_DISCUSSION);


    @Override
    public void createSchedule() throws IOException {
        List<Talk> baseSchedule = createBaseSchedule();
        List<Talk> fixSchedule = baseSchedule
                .parallelStream()
                .filter(talk -> STATIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(talk.getType())))
                .collect(Collectors.toList());
        List<Talk> dynamicSchedule = baseSchedule
                .parallelStream()
                .filter(talk -> DYNAMIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(talk.getType())))
                .collect(Collectors.toList());

        List<GroupTalk> baseGroupTalk = createBaseGroupTalk(fixSchedule);
        List<GroupTalk> dynamicGroupTalks = createGroupTalks(fixSchedule);
        List<Talk> unavailableTalks = addTalksToGroupTalks(dynamicSchedule, dynamicGroupTalks);
        List<GroupTalk> groupTalks = new ArrayList<>();
        groupTalks.addAll(baseGroupTalk);
        groupTalks.addAll(dynamicGroupTalks);

        Map<String, List<Talk>> schedule = createFinalSchedule(groupTalks, fixSchedule);

        schedule.forEach((k, v) -> {
            System.out.println(k + " TRACK 1");
            v.forEach(System.out::println);
        });
    }

    @Override
    public List<Talk> createBaseSchedule() throws IOException {
        String filePath = eventConfiguration.getFilePath();
        Map<String, Long> talkDuration = eventConfiguration.getTalk().getTalkDuration();
        BiPredicate<Talk, Map<String, Long>> talkTypePredicate = (talk, map) -> map.get(talk.getType()) != null;
        List<Talk> baseSchedule = new ArrayList<>();
        baseSchedule.addAll(ScheduleUtil.getTalks(filePath).getValue());
        baseSchedule.forEach(talk -> {
            if (talkTypePredicate.test(talk, talkDuration)) {
                talk.setScheduleTime(setScheduleTime(eventConfiguration, talk.getType()));
            }
        });
        addLunchAndTeaToSchedule(eventConfiguration, baseSchedule);
        return baseSchedule;
    }

    private Map<String, List<Talk>> createFinalSchedule(List<GroupTalk> groupTalks, List<Talk> talks) {
        groupTalks.sort((t1, t2) -> t1.getStartTime().isAfter(t2.getStartTime()) ? 1 : -1);
        Map<String, List<Talk>> finalSchedule = new HashMap<>();
        for (int i = 0; i < getNumberOfDayByClosing(talks); i++) {
            finalSchedule.put(Constant.DAY + (i + 1), new ArrayList<>());
        }
        for (int i = 0; i < groupTalks.size(); ) {
            for (int j = 0; j < getNumberOfDayByClosing(talks); j++) {
                if (i < groupTalks.size()) {
                    finalSchedule.get(Constant.DAY + (j + 1)).addAll(groupTalks.get(i++).getTalks());
                }
            }
        }

        //sort
        for (int i = 0; i < getNumberOfDayByClosing(talks); i++) {
            finalSchedule.get(Constant.DAY + (i + 1)).sort((t1, t2) -> t1.getScheduleTime().getStartTime().isAfter(t2.getScheduleTime().getStartTime()) ? 1 : -1);
        }

        return finalSchedule;
    }

    private List<Talk> addTalksToGroupTalks(List<Talk> dynamicSchedule, List<GroupTalk> groupTalks) {
        dynamicSchedule.sort((t1, t2) -> t1.getScheduleTime().getDuration() <= t2.getScheduleTime().getDuration() ? 1 : -1);
        Iterator iterator = dynamicSchedule.iterator();
        boolean isNext = true;
        Talk talk = null;
        List<Talk> unavailableTalks = new ArrayList<>();
        while (iterator.hasNext()) {
            if (isNext) {
                talk = (Talk) iterator.next();
                isNext = false;
            }
            for (GroupTalk groupTalk : groupTalks) {
                long time = groupTalk.getAvailableTimes();
                long total = groupTalk.getTotalTimes();
                if (talk != null && talk.getScheduleTime().getDuration() <= groupTalk.getAvailableTimes()) {
                    groupTalk.setAvailableTimes(time - talk.getScheduleTime().getDuration());

                    talk.getScheduleTime().setStartTime(groupTalk.getStartTime().plusMinutes(total - time));

                    talk.getScheduleTime().setEndTime(talk.getScheduleTime().getStartTime().plusMinutes(talk.getScheduleTime().getDuration()));

                    groupTalk.getTalks().add(talk);

                    isNext = true;
                    talk = null;
                }
            }
            if (talk != null) {
                unavailableTalks.add(talk);
                talk = null;
                isNext = true;
            }
        }

        System.out.println(unavailableTalks);
        return unavailableTalks;
    }

    private List<GroupTalk> createGroupTalks(List<Talk> fixSchedule) {
        List<GroupTalk> groupTalks = new ArrayList<>();
        List<ScheduleTime> timeband = createTimeBand(fixSchedule);
        long numberOfDays = getNumberOfDayByClosing(fixSchedule);
        for (int i = 0; i < numberOfDays; i++) {
            for (int j = 0; j < timeband.size() - 1; j++) {
                GroupTalk groupTalk = new GroupTalk();
                groupTalk.setStartTime(timeband.get(j).getEndTime());
                groupTalk.setTotalTimes(Duration.between(timeband.get(j).getEndTime(), timeband.get(j + 1).getStartTime()).toMinutes());
                groupTalk.setAvailableTimes(groupTalk.getTotalTimes());
                groupTalks.add(groupTalk);
            }
        }
        return groupTalks;
    }

    private List<GroupTalk> createBaseGroupTalk(List<Talk> staticSchedule) {
        List<GroupTalk> groupTalks = new ArrayList<>();
        staticSchedule.forEach(talk -> {
            GroupTalk groupTalk = new GroupTalk();
            groupTalk.setStartTime(talk.getScheduleTime().getStartTime());
            groupTalk.setTotalTimes(talk.getScheduleTime().getDuration());
            groupTalk.setAvailableTimes(groupTalk.getTotalTimes());
            groupTalk.getTalks().add(talk);
            groupTalks.add(groupTalk);
        });
        return groupTalks;
    }

    private List<ScheduleTime> createTimeBand(List<Talk> fixSchedule) {
        Set<ScheduleTime> set = new HashSet<>();
        fixSchedule.forEach(talk -> set.add(talk.getScheduleTime()));
        List<ScheduleTime> scheduleTimes = new ArrayList<>(set);
        scheduleTimes.sort((t1, t2) -> t1.getStartTime().isAfter(t2.getStartTime()) ? 1 : -1);
        return scheduleTimes;
    }


    private ScheduleTime setScheduleTime(EventConfiguration eventConfiguration, String talkType) {
        ScheduleTime scheduleTime;
        LocalTime startTime = LocalTime.parse(eventConfiguration.getFirstTalkTime());
        LocalTime closingTime = LocalTime.parse(eventConfiguration.getLastTalkTime());
        Long duration = eventConfiguration.getTalk().getTalkDuration().get(talkType);
        switch (Constant.TALK_TYPE.valueOf(talkType)) {
            case KEYNOTE:
                scheduleTime = new ScheduleTime(startTime, startTime.plusMinutes(duration), duration);
                break;
            case CLOSING:
                scheduleTime = new ScheduleTime(closingTime, startTime.plusMinutes(duration), duration);
                break;
            default:
                scheduleTime = new ScheduleTime(null, null, duration);
                break;
        }
        return scheduleTime;
    }

    private void addLunchAndTeaToSchedule(EventConfiguration eventConfiguration, List<Talk> talks) {
        long numberOfDays = getNumberOfDayByClosing(talks);
        for (int i = 0; i < numberOfDays; i++) {
            talks.add(ScheduleUtil.getLunch(eventConfiguration));
            talks.add(ScheduleUtil.getTea(eventConfiguration));
        }
    }

    private long getNumberOfDayByClosing(List<Talk> talks) {
        return talks.parallelStream()
                .filter(talk -> Constant.TALK_TYPE.CLOSING.name().equalsIgnoreCase(talk.getType()))
                .count();
    }

}
