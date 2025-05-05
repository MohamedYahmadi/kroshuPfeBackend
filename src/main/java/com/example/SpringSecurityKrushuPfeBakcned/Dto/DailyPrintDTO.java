package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyPrintDTO {
    private String day;
    private String value;
}
