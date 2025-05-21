package com.example.SpringSecurityKrushuPfeBakcned.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WasteReasonUpdateRequest {
    private int userId;
    private String newReason;
}
