package com.conference.beans;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

@Data
public class Talks extends LinkedHashMap<String, List<Talk>> {
    private final String key = "talks";

    public List<Talk> getValue() {
        return this.get(key);
    }
}
