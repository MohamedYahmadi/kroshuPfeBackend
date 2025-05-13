package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class wasteReasonRequest {
    private List<String> reasons;
    private String departmentName;  // Optional for admins, ignored for normal users
}
