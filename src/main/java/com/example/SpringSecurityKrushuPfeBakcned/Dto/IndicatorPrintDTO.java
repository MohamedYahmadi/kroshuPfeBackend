package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IndicatorPrintDTO {
    private int id;
    private String name;
    private String target;
    private List<WeeklyPrintDTO> weeklyData;
}

