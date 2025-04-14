package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetIndicatorValueDTO {
    private String departmentName;
    private String indicatorName;
    private String value;

}
