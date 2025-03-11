package com.example.SpringSecurityKrushuPfeBakcned.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "viewer")
public class Viewer extends User{

    public Viewer() {
    }

    public Viewer(String firstName, String lastName, String email, String password, String role, String registrationNumber, String department) {
        super(firstName, lastName, email, password, role, registrationNumber, department);
    }
}
