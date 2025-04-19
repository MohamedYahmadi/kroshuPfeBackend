package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndicatorWithoutValuesDTO {
    private int id;
    private String name;
    private String targetPerWeek;
}