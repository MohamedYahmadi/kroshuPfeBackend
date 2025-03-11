package com.example.SpringSecurityKrushuPfeBakcned.Controllers;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.LoginDto;
import com.example.SpringSecurityKrushuPfeBakcned.Dto.ChangePasswordDto;
import com.example.SpringSecurityKrushuPfeBakcned.Dto.SignupDto;
import com.example.SpringSecurityKrushuPfeBakcned.Dto.UpdateProfileDto;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Services.UserService;
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


    @PostMapping("/update-password/{userId}")
    public String updatePassword(@PathVariable int userId, @RequestBody ChangePasswordDto changePasswordData) {
        return userService.changePassword(userId, changePasswordData);
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id, @RequestBody UpdateProfileDto updateProfileDto) {
        return userService.updateProfile(id, updateProfileDto);
    }





}
