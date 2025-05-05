package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AdminUpdateIndicatorValueDTO {
    private String departmentName;
    private String indicatorName;
    private String date;
    private String newValue;
}
