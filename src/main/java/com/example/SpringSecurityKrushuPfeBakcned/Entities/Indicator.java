package com.example.SpringSecurityKrushuPfeBakcned.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Indicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String targetPerWeek;

    @Temporal(TemporalType.DATE)
    private Date day;

    private String dayValue;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;


    @PreUpdate
    @PrePersist
    protected void setDay(){
        this.day =new Date();
    }

}