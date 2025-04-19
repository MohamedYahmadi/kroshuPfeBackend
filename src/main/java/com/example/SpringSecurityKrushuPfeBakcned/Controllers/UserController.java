package com.example.SpringSecurityKrushuPfeBakcned.Controllers;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Services.IndicatorService;
import com.example.SpringSecurityKrushuPfeBakcned.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;
    private final IndicatorService indicatorService;

    @Autowired
    public UserController(UserService userService, IndicatorService indicatorService) {
        this.userService = userService;
        this.indicatorService = indicatorService;
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
public ResponseEntity<LoginResponseDto> loginAdmin(@RequestBody LoginDto loginData) {
    return userService.loginUser(loginData);
}

    @PostMapping("/update-password/{userId}")
    public String updatePassword(@PathVariable int userId, @RequestBody ChangePasswordEmailDto changePasswordData) {
        return userService.changePassword(userId, changePasswordData);
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id, @RequestBody UpdateUserProfileDto updateUserProfileDto) {
        return userService.updateProfile(id, updateUserProfileDto);
    }
    @PostMapping("/request-password-reset")
    public String requestPasswordReset(@RequestBody PasswordResetRequestDto request) {
        return userService.requestPasswordReset(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto request) {
        try {
            String result = userService.resetPassword(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/set-team-member-indicator-value/{userId}")
    public ResponseEntity<IndicatorValueResponseDTO> setTeamMemberIndicatorValue(
            @RequestBody TeamMemberSetIndicatorValueDTO requestDTO,
            @PathVariable int userId) {
        return indicatorService.setTeamMemberIndicatorValue(requestDTO, userId);
    }





}
