package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Setter
@Getter
public class DailyValueDto {
    private String date;
    private String value;

}
