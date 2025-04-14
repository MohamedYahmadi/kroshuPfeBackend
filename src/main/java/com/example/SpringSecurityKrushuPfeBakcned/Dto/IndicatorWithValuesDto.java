package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@AllArgsConstructor
@Getter
@Setter
public class IndicatorWithValuesDto {
    private int id;
    private String name;
    private String targetPerWeek;
    private List<DailyValueDto> dailyValues;
}
