package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Admin;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.AdminRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.TeamMemberRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.UserRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.ViewerRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Security.JwtService;
import com.example.SpringSecurityKrushuPfeBakcned.Util.ResetCodeUtil;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ViewerRepository viewerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;
    private final ResetCodeUtil resetCodeUtil;

    private final EmailService emailService;


    public UserService(UserRepository userRepository, AdminRepository adminRepository, ViewerRepository viewerRepository, TeamMemberRepository teamMemberRepository, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, JwtService jwtService, ResetCodeUtil resetCodeUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.viewerRepository = viewerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.resetCodeUtil = resetCodeUtil;
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

            SendMailDto sendMailDto = new SendMailDto();
            sendMailDto.setEmail(signData.getEmail());
            sendMailDto.setName(signData.getFirstName());
            sendMailDto.setPassword(signData.getPassword());

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
            return ResponseEntity.ok(new LoginResponseDto(token, user.getRole(), user.getId(),user.getDepartment()));
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

    public String requestPasswordReset(PasswordResetRequestDto request) {
        String email = request.getEmail();

        // Check if the user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Generate a random 6-digit reset code
        String resetCode = String.format("%06d", new Random().nextInt(999999));

        // Hash the reset code
        String hashedResetCode = passwordEncoder.encode(resetCode);

        // Print debug information
        System.out.println("Raw Reset Code: " + resetCode);
        System.out.println("Hashed Reset Code: " + hashedResetCode);

        // Set the reset code and expiry time (e.g., 10 minutes from now)
        user.setResetCode(hashedResetCode);
        user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // Send the raw reset code via email
        try {
            emailService.sendResetEmail(email, resetCode); // Send the raw reset code
            return "Reset code sent to your email";
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }


    public String resetPassword(PasswordResetDto request) {
        String resetCode = request.getResetCode();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        List<User> usersWithResetCode = userRepository.findByResetCodeIsNotNull();

        User user = usersWithResetCode.stream()
                .filter(u -> {
                    if (u.getResetCode() == null || u.getResetCodeExpiry() == null) {
                        System.out.println("No reset code or expiry time found for user: " + u.getEmail());
                        return false;
                    }

                    if (LocalDateTime.now().isAfter(u.getResetCodeExpiry())) {
                        System.out.println("Reset code has expired for user: " + u.getEmail());
                        return false;
                    }

                    boolean isMatch = passwordEncoder.matches(resetCode, u.getResetCode());
                    System.out.println("User Email: " + u.getEmail());
                    System.out.println("Stored Reset Code: " + u.getResetCode());
                    System.out.println("Provided Reset Code: " + resetCode);
                    System.out.println("Reset Code Match: " + isMatch);
                    return isMatch;
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset code"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetCode(null);
        user.setResetCodeExpiry(null);
        userRepository.save(user);

        return "Password reset successfully";
    }






    }
    







