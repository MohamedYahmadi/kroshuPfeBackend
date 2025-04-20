package com.example.SpringSecurityKrushuPfeBakcned.Dto;

    import lombok.Getter;
    import lombok.Setter;

    @Setter
    @Getter
    public class UpdateTargetPerWeekDto {
        private int indicatorId;
        private String newName;
        private String newTargetPerWeek;
    }