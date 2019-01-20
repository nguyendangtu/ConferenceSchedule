package com.conference.constants;

public interface Constant {
    String JSON_SRC_FILE_PATH = "talks.json";
    String HH_MM = "HH:mm";
    String DAY = "Day ";

    enum TALK_TYPE {
        KEYNOTE, WORKSHOP, REGULAR_TALK, LUNCH, TEA, LIGHTNING, CLOSING
    }
}