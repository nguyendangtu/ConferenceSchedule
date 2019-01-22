package com.conference.constants;

/***
 * Constant define all constant for Conference
 */
public interface Constant {
    /**
     * Path for sourcing file
     */
    String JSON_SRC_FILE_PATH = "talks.json";

    /**
     * Local time format
     */
    String HH_MM = "HH:mm";

    /**
     * Label for a conference day
     */
    String DAY = "Day ";

    /**
     * talk type define all type of a conference
     */
    enum TALK_TYPE {
        KEYNOTE, WORKSHOP, REGULAR_TALK, LUNCH, TEA, LIGHTNING, PANEL_DISCUSSION, CLOSING
    }
}