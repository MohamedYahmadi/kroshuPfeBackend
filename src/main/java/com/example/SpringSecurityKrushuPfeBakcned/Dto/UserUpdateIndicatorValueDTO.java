package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserUpdateIndicatorValueDTO {
    private String indicatorName;
    private String date;
    private String newValue;
}
