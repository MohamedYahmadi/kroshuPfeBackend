package com.example.SpringSecurityKrushuPfeBakcned.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "department",fetch = FetchType.LAZY)
    private List<Indicator> indicators = new ArrayList<>();
}
