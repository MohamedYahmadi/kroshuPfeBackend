package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class WasteReasonDto {
        private int id;
        private String reason;
        private String departmentName;  // Optional: Add if needed
        private LocalDateTime createdAt;

}
