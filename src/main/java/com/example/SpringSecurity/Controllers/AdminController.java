package com.example.SpringSecurity.Controllers;

import com.example.SpringSecurity.Dto.LoginDto;
import com.example.SpringSecurity.Dto.SignupDto;
import com.example.SpringSecurity.Entities.User;
import com.example.SpringSecurity.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final AdminService adminService;


    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }




    @PostMapping("/create-user")
    public String createUser(@RequestBody SignupDto signData) {
        return adminService.createUser(signData);
    }
}


