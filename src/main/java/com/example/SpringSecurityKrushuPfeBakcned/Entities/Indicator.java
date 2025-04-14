package com.example.SpringSecurityKrushuPfeBakcned.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Indicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String targetPerWeek;


    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "indicator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyValue> dailyValues = new ArrayList<>();


    public void addDailyValue(Date day, String value) {
        DailyValue dailyValue = DailyValue.builder()
                .day(day)
                .value(value)
                .indicator(this)
                .build();
        this.dailyValues.add(dailyValue);
    }
}