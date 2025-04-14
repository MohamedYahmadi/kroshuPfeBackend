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
public class DailyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    private Date day;

    private String value;

    @ManyToOne
    @JoinColumn(name = "indicator_id")
    private Indicator indicator;
}