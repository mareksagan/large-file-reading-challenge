package com.recruitment.temperatures.temperatures.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "temperatures")
public class Temperature {
    @Id
    @GeneratedValue
    private Long id;
    private String city;
    private Date date;
    private Double value;

    public Temperature(Long id, String city, Date date, Double value) {
        this.id = id;
        this.city = city;
        this.date = date;
        this.value = value;
    }
    public Temperature(String city, Date date, Double value) {
        this.city = city;
        this.date = date;
        this.value = value;
    }
    public Temperature(){

    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
