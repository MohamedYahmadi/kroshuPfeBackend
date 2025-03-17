package com.example.SpringSecurityKrushuPfeBakcned.Dto;

public class LoginResponseDto {
    public String token;
    public String role;
    public int id;

    public LoginResponseDto(String token, String role, int id) {
        this.token = token;
        this.role = role;
        this.id = id;
    }
}
