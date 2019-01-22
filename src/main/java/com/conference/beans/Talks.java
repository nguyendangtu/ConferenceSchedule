package com.conference.beans;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/***
 * Talks is a LinkedHashMap object which can change base on input Json Structure.
 */
@Data
public class Talks extends LinkedHashMap<String, List<Talk>> {
    private final String key = "talks";

    public List<Talk> getValue() {
        return this.get(key);
    }
}
