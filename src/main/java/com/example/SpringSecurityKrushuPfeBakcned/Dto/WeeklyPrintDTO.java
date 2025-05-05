package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WeeklyPrintDTO {
    private String weekLabel;
    private String dateRange;
    private List<DailyPrintDTO> dailyValues;
}

