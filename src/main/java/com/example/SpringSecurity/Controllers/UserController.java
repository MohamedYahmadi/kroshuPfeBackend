package com.example.SpringSecurity.Controllers;

import com.example.SpringSecurity.Dto.LoginDto;
import com.example.SpringSecurity.Dto.ResetPasswordDto;
import com.example.SpringSecurity.Dto.SignupDto;
import com.example.SpringSecurity.Dto.UpdateProfileDto;
import com.example.SpringSecurity.Entities.User;
import com.example.SpringSecurity.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup-admin")
    public String signUpAdmin(@RequestBody SignupDto signData) {
        return userService.signUpAdmin(signData);
    }

   @GetMapping("/profile/{id}")
   public ResponseEntity<User> getProfile(@PathVariable int id) {
       return userService.getProfile(id);
   }

    @PostMapping("login")
    public String LoginAdmin( @RequestBody LoginDto loginData){
        return userService.loginUser(loginData);}



    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return userService.resetPassword(resetPasswordDto);
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id, @RequestBody UpdateProfileDto updateProfileDto) {
        return userService.updateProfile(id, updateProfileDto);
    }





}
