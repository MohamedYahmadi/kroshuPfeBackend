package com.example.SpringSecurity.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")

public class Admin extends User{



    public Admin() {
    }


    public Admin(String firstname, String lastName, String email, String password, String role, String registrationNumber, String department) {
        super(firstname, lastName, email, password, role, registrationNumber, department);
    }



}
