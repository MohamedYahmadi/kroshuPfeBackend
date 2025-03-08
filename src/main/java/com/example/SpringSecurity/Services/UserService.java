package com.example.SpringSecurity.Services;

import com.example.SpringSecurity.Dto.LoginDto;
import com.example.SpringSecurity.Dto.ResetPasswordDto;
import com.example.SpringSecurity.Dto.SignupDto;
import com.example.SpringSecurity.Dto.UpdateProfileDto;
import com.example.SpringSecurity.Entities.Admin;
import com.example.SpringSecurity.Entities.TeamMember;
import com.example.SpringSecurity.Entities.User;
import com.example.SpringSecurity.Entities.Viewer;
import com.example.SpringSecurity.Respositories.AdminRepository;
import com.example.SpringSecurity.Respositories.TeamMemberRepository;
import com.example.SpringSecurity.Respositories.UserRepository;
import com.example.SpringSecurity.Respositories.ViewerRepository;
import com.example.SpringSecurity.Security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ViewerRepository viewerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, AdminRepository adminRepository, ViewerRepository viewerRepository, TeamMemberRepository teamMemberRepository, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, JwtService jwtService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.viewerRepository = viewerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
    }


    public String signUpAdmin(SignupDto signData) {

        Optional<Admin> oldAdmin = adminRepository.findAll().stream()
                .filter(admin -> "Admin".equalsIgnoreCase(admin.getRole()))
                .findFirst();
        if (oldAdmin.isPresent()) {
            throw new IllegalStateException("An Admin Account Already Exists");
        }

        String encodedPassword = passwordEncoder.encode(signData.getPassword());

        Admin admin = new Admin(
                signData.getFirstName(),
                signData.getLastName(),
                signData.getEmail(),
                encodedPassword,
                "Admin",
                signData.getRegistrationNumber(),
                signData.getDepartment()
        );

        adminRepository.save(admin);
        return "Admin Account Created Successfully";
    }


    public ResponseEntity<User> getProfile(int userId){
        Optional<User> userOptional =userRepository.findById(userId);
        if (userOptional.isPresent()){
            return ResponseEntity.ok(userOptional.get());

        }else {
            return ResponseEntity.status(404).body(null);
        }
    }

    public String loginUser(LoginDto loginData) {
        try {
            User user = userRepository.findByEmail(loginData.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + loginData.getEmail()));

            if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Incorrect password");
            }

            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword())
            );

            // Generate and return the JWT token
            return jwtService.createToken(loginData.getEmail());
        } catch (Exception e) {
            System.out.println(e);
            return "Email or password incorrect";
        }
    }



    public String resetPassword(ResetPasswordDto resetPasswordDto) {

        User user = userRepository.findByEmail(resetPasswordDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + resetPasswordDto.getEmail()));

        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
            throw new BadCredentialsException("Passwords do not match");
        }

        String encodedPassword = passwordEncoder.encode(resetPasswordDto.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return "Password reset successfully";
    }
    public ResponseEntity<String> updateProfile(int userId, UpdateProfileDto updateProfileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setFirstName(updateProfileDto.getFirstName());
        user.setLastName(updateProfileDto.getLastName());
        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }




}

