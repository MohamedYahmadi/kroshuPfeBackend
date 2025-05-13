package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionItemDto {
    private int id;
    private String action;
    private String departmentName;  // Add this field
    private LocalDateTime createdAt;

}
