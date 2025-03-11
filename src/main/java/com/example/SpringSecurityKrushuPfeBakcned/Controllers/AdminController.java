package com.example.SpringSecurityKrushuPfeBakcned.Controllers;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.SignupDto;
import com.example.SpringSecurityKrushuPfeBakcned.Dto.UpdateAdminProfileDto;
import com.example.SpringSecurityKrushuPfeBakcned.Services.AdminService;
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

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id, @RequestBody UpdateAdminProfileDto updateAdminProfileDto) {
        return adminService.updateProfile(id, updateAdminProfileDto);
    }
}


