package com.example.SpringSecurityKrushuPfeBakcned.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "teammember")
public class TeamMember extends User{

    public TeamMember() {
    }

    public TeamMember(String firstName, String lastName, String email, String password, String role, String registrationNumber, String department) {
        super(firstName, lastName, email, password, role, registrationNumber, department);
    }
}
