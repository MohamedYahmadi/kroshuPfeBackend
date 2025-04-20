package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class IndicatorHistoryDTO {
    private int id;
    private String name;
    private String target;
    private List<WeeklyDataDTO> weeklyData;
}
