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
public class IndicatorValueResponseDTO {
    private boolean success;
    private String message;
    private Date day;
    private String value;
}