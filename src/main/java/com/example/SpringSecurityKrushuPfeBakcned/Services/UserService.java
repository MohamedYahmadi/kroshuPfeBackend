package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Admin;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.AdminRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.TeamMemberRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.UserRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.ViewerRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Security.JwtService;
import jakarta.mail.MessagingException;
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
    private final EmailService emailService;


    public UserService(UserRepository userRepository, AdminRepository adminRepository, ViewerRepository viewerRepository, TeamMemberRepository teamMemberRepository, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, JwtService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.viewerRepository = viewerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;

        this.emailService = emailService;
    }


    public String signUpAdmin(SignupDto signData) {
        try {
            // Check if an admin account already exists
            Optional<Admin> oldAdmin = adminRepository.findAll().stream()
                    .filter(admin -> "Admin".equalsIgnoreCase(admin.getRole()))
                    .findFirst();
            if (oldAdmin.isPresent()) {
                throw new IllegalStateException("An Admin Account Already Exists");
            }

            // Encode the password
            String encodedPassword = passwordEncoder.encode(signData.getPassword());

            // Create the admin entity
            Admin admin = new Admin(
                    signData.getFirstName(),
                    signData.getLastName(),
                    signData.getEmail(),
                    encodedPassword,
                    "Admin",
                    signData.getRegistrationNumber(),
                    signData.getDepartment()
            );

            // Save the admin to the database
            adminRepository.save(admin);

            // Create a SendMailDto object
            SendMailDto sendMailDto = new SendMailDto();
            sendMailDto.setEmail(signData.getEmail());
            sendMailDto.setName(signData.getFirstName());
            sendMailDto.setPassword(signData.getPassword());

            // Send an email with login details
            emailService.sendEmail(sendMailDto);

            return "Admin Account Created Successfully";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Admin Account Created, but failed to send email: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
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



    public String changePassword(int userId, ChangePasswordDto changePasswordData) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(changePasswordData.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        if (!changePasswordData.getNewPassword().equals(changePasswordData.getConfirmPassword())) {
            throw new BadCredentialsException("New passwords do not match");
        }

        String encodedPassword = passwordEncoder.encode(changePasswordData.getNewPassword());
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

