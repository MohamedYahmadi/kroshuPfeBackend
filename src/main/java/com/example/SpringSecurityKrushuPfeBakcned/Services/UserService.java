package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Admin;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.AdminRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.TeamMemberRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.UserRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.ViewerRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Security.JwtService;
import com.example.SpringSecurityKrushuPfeBakcned.Security.ResetTokenService;
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
    private final ResetTokenService resetTokenService;
    private final EmailService emailService;


    public UserService(UserRepository userRepository, AdminRepository adminRepository, ViewerRepository viewerRepository, TeamMemberRepository teamMemberRepository, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, JwtService jwtService, ResetTokenService resetTokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.viewerRepository = viewerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.resetTokenService = resetTokenService;

        this.emailService = emailService;
    }





    public String signUpAdmin(SignupDto signData) {
        try {

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

  public ResponseEntity<LoginResponseDto> loginUser(LoginDto loginData) {
        try {
            User user = userRepository.findByEmail(loginData.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + loginData.getEmail()));

            if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Incorrect password");
            }

            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword())
            );

            String token = jwtService.createToken(loginData.getEmail());
            return ResponseEntity.ok(new LoginResponseDto(token, user.getRole(), user.getId()));
        } catch (Exception e) {
            throw new BadCredentialsException("Incorrect email or password");
        }
    }



   public String changePassword(int userId, ChangePasswordEmailDto changePasswordData) {
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

        ChangePasswordEmailDto passwordChangeMailDto = new ChangePasswordEmailDto();
        passwordChangeMailDto.setEmail(user.getEmail());
        passwordChangeMailDto.setName(user.getFirstName());
        passwordChangeMailDto.setOldPassword(changePasswordData.getOldPassword());
        passwordChangeMailDto.setNewPassword(changePasswordData.getNewPassword());

        // Step 6: Send the email notification
        try {
            emailService.sendPasswordChangeEmail(passwordChangeMailDto);
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Password changed successfully, but failed to send email: " + e.getMessage();
        }

        return "Password changed successfully";
    }


    public ResponseEntity<String> updateProfile(int userId, UpdateUserProfileDto updateUserProfileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setFirstName(updateUserProfileDto.getFirstName());
        user.setLastName(updateUserProfileDto.getLastName());
        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }

    public String requestPasswordReset( PasswordResetRequestDto request) {
        String email = request.getEmail();

        // Check if the user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Generate a reset token
        String resetToken = resetTokenService.generateResetToken(email);

        // Create the reset link
        String resetLink = "yourapp://reset-password    " +
                "token=" + resetToken;

        // Send the reset link via email
        try {
            emailService.sendResetEmail(email, resetLink);
            return "Reset link sent to your email";
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }

    // Step 2: Reset the password
    public String resetPassword( PasswordResetDto request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        // Validate that the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        try {
            // Verify the token
            String email = resetTokenService.verifyResetToken(token);

            // Find the user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Encode and save the new password
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);

            return "Password reset successfully";
        } catch (RuntimeException e) {
            // Handle token expiration or invalid token
            throw new RuntimeException("Invalid or expired token. Please request a new reset link.");
        } catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("An error occurred while resetting the password: " + e.getMessage());
        }
    }
    





}

