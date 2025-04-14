package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class DepartmentIndicatorsDTO {
    private int departmentId;  // New field
    private String departmentName;
    private List<IndicatorWithValuesDto> indicators;

    public DepartmentIndicatorsDTO(String departmentName, List<IndicatorWithValuesDto> indicators) {
        this(0, departmentName, indicators); // Call new constructor with null ID
    }

    public DepartmentIndicatorsDTO(int departmentId, String departmentName, List<IndicatorWithValuesDto> indicators) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.indicators = indicators;
    }
}

