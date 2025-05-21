package com.example.SpringSecurityKrushuPfeBakcned.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<Indicator> indicators = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<WasteReason> wasteReasons = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<ActionItem> actionItems = new HashSet<>();
}

