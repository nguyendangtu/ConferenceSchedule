package com.conference.configurations;

import com.conference.utils.DateUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.validation.Valid;
import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "event")
@Data
public class EventConfiguration {

    private String filePath;

    private String firstTalkTime;

    private String lastTalkTime;

    @Valid
    private final Lunch lunch = new Lunch();

    @Valid
    private final Tea tea = new Tea();

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

    public long getDuration() {
        if (null != firstTalkTime && null != lastTalkTime) {
            return DateUtil.getDuration(firstTalkTime, lastTalkTime);
        }
        return -1;
    }

}
