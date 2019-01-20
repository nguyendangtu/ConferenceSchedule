package com.conference.databind;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScheduleObjectMapper {

    public <T> T readValue(String srcPath, TypeReference valueTypeRef) throws IOException {

        //create ScheduleObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //read json file data to String
        byte[] jsonData = Files.readAllBytes(Paths.get(srcPath));

        //convert json string to object
        return objectMapper.readValue(jsonData, valueTypeRef);
    }
}
