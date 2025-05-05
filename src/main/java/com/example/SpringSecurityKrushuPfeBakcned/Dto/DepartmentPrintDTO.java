package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DepartmentPrintDTO {
    private int id;
    private String name;
    private String printDate;
    private List<IndicatorPrintDTO> indicators;
}
