package com.recruitment.temperatures.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class AverageTemperature {
    private Integer year;
    @JsonSerialize(using = OneDecimalSerializer.class)
    private Double averageTemperature;

    public AverageTemperature(Integer year, Double averageTemperature) {
        this.year = year;
        this.averageTemperature = averageTemperature;
    }


    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    private static class OneDecimalSerializer extends JsonSerializer<Double> {
        @Override
        public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (null == value) {
                jgen.writeNull();
            } else {
                BigDecimal bd = new BigDecimal(value).setScale(1, RoundingMode.FLOOR);
                jgen.writeNumber(bd.doubleValue());
            }
        }
    }
}
