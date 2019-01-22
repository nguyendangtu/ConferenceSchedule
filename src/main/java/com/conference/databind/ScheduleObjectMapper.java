package com.conference.databind;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/***
 * ScheduleObjectMapper will use JACKSON libs to implement mapper and parser from Json to an object
 * for Conference Schedule Project
 */
public class ScheduleObjectMapper {

    /**
     * Read value will read value from file base on file path and type reference
     *
     * @param srcPath
     * @param valueTypeRef
     * @param <T>
     * @return return an object with provide @TypeReference
     * @throws IOException
     */
    public <T> T readValue(String srcPath, TypeReference valueTypeRef) throws IOException {

        //create ScheduleObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //read json file data to String
        byte[] jsonData = Files.readAllBytes(Paths.get(srcPath));

        //convert json string to object
        return objectMapper.readValue(jsonData, valueTypeRef);
    }
}
