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
public class DepartementDto {
    private int departmentId;
    private String departmentName;
    private List<IndicatorWithValuesDto> indicators;
}
