package com.conference.service.impl;

import com.conference.beans.ScheduleTime;
import com.conference.beans.Talk;
import com.conference.configurations.EventConfiguration;
import com.conference.constants.Constant;
import com.conference.service.ScheduleService;
import com.conference.utils.ScheduleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

        Map<String, List<Talk>> schedule = createSchedule(fixSchedule);
        List<ScheduleTime> timeband = createTimeBand(fixSchedule);
        addDynamicScheduleToSchedule(schedule, dynamicSchedule, timeband);

        schedule.forEach((k, v) -> {
            System.out.println(k + " TRACK 1");
            v.sort((t1, t2) -> t1.getScheduleTime().getStartTime().isAfter(t2.getScheduleTime().getStartTime()) ? 1 : -1);
            v.forEach(System.out::println);
        });


    }

    private List<ScheduleTime> createTimeBand(List<Talk> fixSchedule) {
        Set<ScheduleTime> set = new HashSet<>();
        fixSchedule.forEach(talk -> set.add(talk.getScheduleTime()));
        List<ScheduleTime> scheduleTimes = new ArrayList<>(set);
        scheduleTimes.sort((t1, t2) -> t1.getStartTime().isAfter(t2.getStartTime()) ? 1 : -1);
        return scheduleTimes;
    }

    private void addDynamicScheduleToSchedule(Map<String, List<Talk>> schedule, List<Talk> dynamicSchedule, List<ScheduleTime> staticTimeBand) {
        ScheduleTime keyNote = staticTimeBand.get(0);
        ScheduleTime[] previous = new ScheduleTime[]{keyNote};
        List<Talk> remainTalk = new ArrayList<>();
        Iterator<Talk> dynamicIterator = dynamicSchedule.iterator();
        while (dynamicIterator.hasNext()) {
            Talk talk = dynamicIterator.next();
            final boolean[] isAdded = new boolean[]{false};
            schedule.forEach((key, value) -> {
                if (!isAdded[0]) {
                    if (!value.stream().anyMatch(item -> DYNAMIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(item.getType())))) {
                        talk.getScheduleTime().setStartTime(keyNote.getEndTime());
                        talk.getScheduleTime().setEndTime(keyNote.getEndTime().plusMinutes(talk.getScheduleTime().getDuration()));
                        value.add(talk);
                        isAdded[0] = true;
                    } else if (ScheduleUtil.checkScheduleTime(previous[0], talk.getScheduleTime().getDuration(), staticTimeBand)) {
                        talk.getScheduleTime().setStartTime(previous[0].getEndTime());
                        talk.getScheduleTime().setEndTime(previous[0].getEndTime().plusMinutes(talk.getScheduleTime().getDuration()));
                        value.add(talk);
                        isAdded[0] = true;
                    } else {
                        remainTalk.add(talk);
                        //isAdded[0] = true;
                    }
                }
            });
            if (isAdded[0]) {
                previous[0] = talk.getScheduleTime();
            }
        }

        schedule.forEach((k, v) -> {
            System.out.println(k + " TRACK 1");
            v.sort((t1, t2) -> t1.getScheduleTime().getStartTime().isAfter(t2.getScheduleTime().getStartTime()) ? 1 : -1);
            v.forEach(System.out::println);
        });
        if (!remainTalk.isEmpty()) {
            addDynamicScheduleToSchedule(schedule, remainTalk, staticTimeBand);
        }
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

    private Map<String, List<Talk>> createSchedule(List<Talk> staticSchedule) {
        Map<String, List<Talk>> schedule = new HashMap<>();
        for (int i = 0; i < getNumberOfDayByKeyNote(staticSchedule); i++) {
            List<Talk> items = new ArrayList<>();
            staticSchedule.forEach(talk -> {
                if (!items.stream().anyMatch(item -> item.getType().equalsIgnoreCase(talk.getType()))) {
                    items.add(talk);
                }
            });
            schedule.put(Constant.DAY + (i + 1), items);
        }
        return schedule;
    }

    private void addLunchAndTeaToSchedule(EventConfiguration eventConfiguration, List<Talk> talks) {
        long numberOfDays = getNumberOfDayByKeyNote(talks);
        for (int i = 0; i < numberOfDays; i++) {
            talks.add(ScheduleUtil.getLunch(eventConfiguration));
            talks.add(ScheduleUtil.getTea(eventConfiguration));
        }
    }

    private long getNumberOfDayByKeyNote(List<Talk> talks) {
        return talks.parallelStream()
                .filter(talk -> Constant.TALK_TYPE.KEYNOTE.name().equalsIgnoreCase(talk.getType()))
                .count();
    }

}
