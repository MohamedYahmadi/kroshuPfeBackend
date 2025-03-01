package com.example.SpringSecurity.Entities;

import jakarta.persistence.Entity;

@Entity
public class Student extends User{

    private String className;

    public Student() {
    }


    public Student(String name, String email, String password, String className) {
        super(name, email, password);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
