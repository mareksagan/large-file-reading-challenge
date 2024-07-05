package com.recruitment.temperatures.models;

import com.recruitment.temperatures.assertions.Assert;
import com.recruitment.temperatures.temperatures.jpa.Temperature;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public record TemperatureCreator(String csvLine, String csvDelimiter) {
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public TemperatureCreator {
        Assert.notBlank("csvLine", csvLine);
        Assert.field("csvLength", csvLine.split(csvDelimiter).length)
                .max(3)
                .min(3);
    }
    public Temperature create()  {
        String[] values = csvLine.split(csvDelimiter);
        try {
            return new Temperature(values[0], simpleDateFormat.parse(values[1]), Double.valueOf(values[2]));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
