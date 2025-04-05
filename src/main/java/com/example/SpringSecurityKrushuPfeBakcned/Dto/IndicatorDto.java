package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class IndicatorDto {
    private Integer id;
    private String name;
    private String targetPerWeek;
    private String day;
    private String dayValue;

    public IndicatorDto(Integer id, String name, String targetPerWeek, String day, String dayValue) {
        this.id = id;
        this.name = name;
        this.targetPerWeek = targetPerWeek;
        this.day = day;
        this.dayValue = dayValue;
    }
}