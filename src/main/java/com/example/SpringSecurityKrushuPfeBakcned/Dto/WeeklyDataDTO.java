package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WeeklyDataDTO {
    private String weekLabel;
    private String dateRange;
    private Map<String, String> dailyValues;
}
