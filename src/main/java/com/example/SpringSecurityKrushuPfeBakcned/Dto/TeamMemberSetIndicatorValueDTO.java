package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeamMemberSetIndicatorValueDTO {
    private String indicatorName;
    private String value;
}
