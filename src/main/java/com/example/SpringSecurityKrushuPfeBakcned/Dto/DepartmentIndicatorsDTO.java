package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class DepartmentIndicatorsDTO {
    private String departmentName;
    private List<IndicatorDto> indicators;


    public DepartmentIndicatorsDTO(String departmentName, List<IndicatorDto> indicators) {
        this.departmentName = departmentName;
        this.indicators = indicators;
    }
}

