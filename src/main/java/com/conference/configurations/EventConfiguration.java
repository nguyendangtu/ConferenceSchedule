package com.conference.configurations;

import com.conference.utils.DateUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.validation.Valid;
import java.util.Map;

/***
 * EventConfiguration load all configs for a conference schedule from properties file when application started
 */
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "event")
@Data
public class EventConfiguration {

    /**
     * path for sourcing file
     */
    private String filePath;

    /**
     * time start a conference daily
     */
    private String firstTalkTime;

    /**
     * time ending for a conference daily
     */
    private String lastTalkTime;

    /**
     * Lunch is stored lunch schedule time which load from properties file
     */
    @Valid
    private final Lunch lunch = new Lunch();

    /**
     * Tea is stored tea schedule time which load from properties file
     */
    @Valid
    private final Tea tea = new Tea();

    /**
     * Talk is a stored duration mapping for all talks
     */
    @Valid
    private final Talk talk = new Talk();

    @Data
    public static class Lunch {
        private String startTime;
        private String endTime;
        private long duration;
    }

    @Data
    public static class Tea {
        private String startTime;
        private String endTime;
        private long duration;
    }

    @Data
    public static class Talk {
        private Map<String, Long> talkDuration;
    }

    /**
     * calculate during from start conference and closing conference daily
     *
     * @return long
     */
    public long getDuration() {
        if (null != firstTalkTime && null != lastTalkTime) {
            return DateUtil.getDuration(firstTalkTime, lastTalkTime);
        }
        return -1;
    }

}
