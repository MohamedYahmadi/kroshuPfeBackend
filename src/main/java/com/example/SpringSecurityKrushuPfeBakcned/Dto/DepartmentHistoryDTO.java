package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class DepartmentHistoryDTO {
    private int id;
    private String name;
    private List<IndicatorHistoryDTO> indicators;
}
