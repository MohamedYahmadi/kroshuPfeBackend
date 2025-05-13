package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DepartmentIndicatorsWithActionsDTO {
    private int departmentId;
    private String departmentName;
    private List<IndicatorWithValuesDto> indicators;
    private List<ActionItemDto> actionItems;
    private List<WasteReasonDto> wasteReasons;


}

