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


/***
 * ScheduleService is a service which allowing client use it to create a schedule.
 * the method addTalksToGroupTalks main method help us to setup schedule for talks
 */
@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {

    /**
     * Define eventConfiguration to get all configuration for this class
     */
    @Autowired
    private EventConfiguration eventConfiguration;

    /**
     * Declare STATIC_TALK_TYPE for all static type
     */
    private EnumSet<Constant.TALK_TYPE> STATIC_TALK_TYPE = EnumSet.of(Constant.TALK_TYPE.KEYNOTE,
            Constant.TALK_TYPE.LUNCH,
            Constant.TALK_TYPE.TEA,
            Constant.TALK_TYPE.CLOSING);

    /**
     * Declare DYNAMIC_TALK_TYPE for all static type
     */
    private EnumSet<Constant.TALK_TYPE> DYNAMIC_TALK_TYPE = EnumSet.of(Constant.TALK_TYPE.WORKSHOP,
            Constant.TALK_TYPE.REGULAR_TALK,
            Constant.TALK_TYPE.LIGHTNING,
            Constant.TALK_TYPE.PANEL_DISCUSSION);

    /**
     * Create Schedule for Conference. Schedule will contain fix schedule, dynamic schedule and running parallel schedule
     *
     * @return return a hashmap with key is a conference label for a day, value is talks for this day.
     * @throws IOException
     */
    @Override
    public Map<String, List<Talk>> createSchedule() throws IOException {
        //Create base schedule
        List<Talk> baseSchedule = createBaseSchedule();

        //create fix schedule filter by STATIC_TALK_TYPE
        List<Talk> fixSchedule = baseSchedule.parallelStream()
                .filter(slot -> STATIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(slot.getType())))
                .collect(Collectors.toList());
        //create a dynamic schedule filter by DYNAMIC_TALK_TYPE
        List<Talk> dynamicSchedule = baseSchedule.parallelStream()
                .filter(slot -> DYNAMIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(slot.getType())))
                .collect(Collectors.toList());

        //create base group talk, static talks are added to base group talk
        List<GroupTalk> baseGroupTalk = createBaseGroupTalk(fixSchedule);

        //create dynamic group talk, talk is not added to dynamic group talk
        List<GroupTalk> dynamicGroupTalks = createGroupTalks(fixSchedule);
        //add talk to dynamic group talk, if talk all group is full, leftTalk are returned
        List<Talk> leftTalks = addTalksToGroupTalks(dynamicSchedule, dynamicGroupTalks);

        //Create group talk to add base group talk and dynamic group talk to it
        List<GroupTalk> groupTalks = new ArrayList<>();
        groupTalks.addAll(baseGroupTalk);
        groupTalks.addAll(dynamicGroupTalks);

        //Create parallel group talk base on remain talks
        List<GroupTalk> parallelGroupTalk = createParallelGroupTalk(leftTalks, fixSchedule);

        //Create final schedule which combine normal group talks and parallel group talks
        Map<String, List<Talk>> schedule = createFinalSchedule(groupTalks, parallelGroupTalk, fixSchedule);


        schedule.forEach((k, v) -> {
            System.out.println(k + " TRACK 1");
            v.forEach(System.out::println);
        });

        return schedule;
    }

    /**
     * create base schedule base on json input and configuration, this method will create
     * all talks for a conference, if talk is fixed time, we call it fix schedule talk and set all necessary
     * for it. If talk is flexible time, we need to run the algorithm to set schedule for dynamic talks.
     *
     * @return all talks for a conference
     * @throws IOException
     */
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
        //add lunch and tea talks to schedule
        addLunchAndTeaToSchedule(eventConfiguration, baseSchedule);

        return baseSchedule;
    }

    /***
     * Create final Schedule with create final schedule for a conference base on normal talks and running parallel talks
     * It will return a final schedule for a conference
     * @param groupTalks
     * @param parallelGroupTalk
     * @param talks
     * @return @HashMap<String,List<Talk>
     */
    private Map<String, List<Talk>> createFinalSchedule(List<GroupTalk> groupTalks, List<GroupTalk> parallelGroupTalk, List<Talk> talks) {

        //sort increase normal group talks base on start time
        groupTalks.sort((t1, t2) -> t1.getStartTime().isAfter(t2.getStartTime()) ? 1 : -1);
        //sort increase parallel group talks base on start time
        parallelGroupTalk.sort((t1, t2) -> t1.getStartTime().isAfter(t2.getStartTime()) ? 1 : -1);

        Map<String, List<Talk>> finalSchedule = new HashMap<>();

        long numberOfClosing = getNumberOfDayByClosing(talks);

        //Init hashmap which contain all talks for a conference per day. Each item has key is label day and value is
        //all talks for this day
        for (int i = 0; i < numberOfClosing; i++) {
            finalSchedule.put(Constant.DAY + (i + 1), new ArrayList<>());
        }

        //add talks for each day
        for (int i = 0, t = 0; i < groupTalks.size(); ) {
            for (int j = 0; j < numberOfClosing; j++) {
                if (i < groupTalks.size()) {
                    //add normal talks to schedule per day
                    finalSchedule.get(Constant.DAY + (j + 1)).addAll(groupTalks.get(i).getTalks());
                    //add parallel talks to schedule per day
                    if (t < parallelGroupTalk.size() && parallelGroupTalk.get(t).getStartTime().equals(groupTalks.get(i).getStartTime())) {
                        finalSchedule.get(Constant.DAY + (j + 1)).addAll(parallelGroupTalk.get(t).getTalks());
                        t++;
                    }
                    i++;
                }
            }
        }

        //sort start for all talk for each day.
        for (int i = 0; i < numberOfClosing; i++) {
            finalSchedule.get(Constant.DAY + (i + 1))
                    .sort((t1, t2) -> t1.getScheduleTime().getStartTime().isAfter(t2.getScheduleTime().getStartTime()) ? 1 : -1);
        }

        return finalSchedule;
    }


    /**
     * Create parallel group talk, if still have remain talks, exception will be throw
     *
     * @param leftTalks
     * @param fixSchedule
     * @return
     */
    private List<GroupTalk> createParallelGroupTalk(List<Talk> leftTalks, List<Talk> fixSchedule) {
        List<GroupTalk> dynamicGroupTalk2 = createGroupTalks(fixSchedule);
        List<Talk> dynamicSchedule2 = leftTalks.parallelStream()
                .filter(slot -> DYNAMIC_TALK_TYPE.contains(Constant.TALK_TYPE.valueOf(slot.getType())))
                .collect(Collectors.toList());

        leftTalks = addTalksToGroupTalks(dynamicSchedule2, dynamicGroupTalk2);
        if (leftTalks.size() > 0) {
            throw new RuntimeException("Can NOT schedule all talks!");
        }
        return dynamicGroupTalk2;
    }


    /**
     * Add talk to dynamic group talks and return a list of talks which already schedule
     * This method is a algorithm for this conference schedule.
     * We will loop all talks from high duration to low duration, if talk can be added to group talk, we
     * move to next item, when all group talks are full but still have remain talks, we return remain talks.
     *
     * @param dynamicSchedule
     * @param groupTalks
     * @return List talk
     */
    private List<Talk> addTalksToGroupTalks(List<Talk> dynamicSchedule, List<GroupTalk> groupTalks) {
        //sort talks increase base on durations
        dynamicSchedule.sort((t1, t2) -> t1.getScheduleTime().getDuration() <= t2.getScheduleTime().getDuration() ? 1 : -1);

        Iterator iterator = dynamicSchedule.iterator();
        List<Talk> unavailableTalks = new ArrayList<>();
        while (iterator.hasNext()) {
            Talk talk = (Talk) iterator.next();
            for (GroupTalk groupTalk : groupTalks) {
                long time = groupTalk.getAvailableTimes();
                long total = groupTalk.getTotalTimes();
                if (talk != null && talk.getScheduleTime().getDuration() <= groupTalk.getAvailableTimes()) {
                    groupTalk.setAvailableTimes(time - talk.getScheduleTime().getDuration());

                    talk.getScheduleTime().setStartTime(groupTalk.getStartTime().plusMinutes(total - time));

                    talk.getScheduleTime().setEndTime(talk.getScheduleTime().getStartTime().plusMinutes(talk.getScheduleTime().getDuration()));

                    groupTalk.getTalks().add(talk);

                    talk = null;
                }
            }

            if (talk != null) {
                unavailableTalks.add(talk);
            }
        }

        return unavailableTalks;
    }

    /**
     * Create Group Talks
     *
     * @param fixSchedule
     * @return
     */
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

    /**
     * create base group talk which already time for static talks
     *
     * @param staticSchedule
     * @return
     */
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

    /**
     * create time band base on static talks
     *
     * @param fixSchedule
     * @return
     */
    private List<ScheduleTime> createTimeBand(List<Talk> fixSchedule) {
        Set<ScheduleTime> set = new HashSet<>();
        fixSchedule.forEach(talk -> set.add(talk.getScheduleTime()));
        List<ScheduleTime> scheduleTimes = new ArrayList<>(set);
        scheduleTimes.sort((t1, t2) -> t1.getStartTime().isAfter(t2.getStartTime()) ? 1 : -1);
        return scheduleTimes;
    }

    /**
     * Set schedule time for talks base on talk type
     *
     * @param eventConfiguration
     * @param talkType
     * @return schedule time
     */
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

    /**
     * add lunch and tea to list of talks
     *
     * @param eventConfiguration
     * @param talks
     */
    private void addLunchAndTeaToSchedule(EventConfiguration eventConfiguration, List<Talk> talks) {
        long numberOfDays = getNumberOfDayByClosing(talks);
        for (int i = 0; i < numberOfDays; i++) {
            talks.add(ScheduleUtil.getLunch(eventConfiguration));
            talks.add(ScheduleUtil.getTea(eventConfiguration));
        }
    }

    /**
     * This function return the number of days in a conference base on number of Closing talks.
     *
     * @param talks
     * @return
     */
    private long getNumberOfDayByClosing(List<Talk> talks) {
        return talks.parallelStream().filter(talk -> Constant.TALK_TYPE.CLOSING.name().equalsIgnoreCase(talk.getType())).count();
    }

}
