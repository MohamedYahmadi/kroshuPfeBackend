package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Admin;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.TeamMember;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Viewer;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;
    private final ViewerRepository viewerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EmailService emailService;

    public AdminService(AdminRepository adminRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, JwtService jwtService, ViewerRepository viewerRepository, TeamMemberRepository teamMemberRepository, EmailService emailService) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.viewerRepository = viewerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.emailService = emailService;
    }



    public String createUser(SignupDto signData) {
        try {
            if ("Admin".equalsIgnoreCase(signData.getRole())) {
                throw new IllegalStateException("Cannot create a user with the role 'Admin'");
            }

            if (userRepository.findByEmail(signData.getEmail()).isPresent()) {
                throw new IllegalStateException("Email already used");
            }

            String encodedPassword = passwordEncoder.encode(signData.getPassword());

            switch (signData.getRole().toUpperCase()) {
                case "TEAM_MEMBER":
                    TeamMember teamMember = new TeamMember();
                    setCommonUserFields(teamMember, signData, encodedPassword);
                    teamMemberRepository.save(teamMember);
                    break;

                case "VIEWER":
                    Viewer viewer = new Viewer();
                    setCommonUserFields(viewer, signData, encodedPassword);
                    viewerRepository.save(viewer);
                    break;

                default:
                    throw new IllegalStateException("Invalid role: " + signData.getRole());
            }

            SendMailDto sendMailDto = new SendMailDto();
            sendMailDto.setEmail(signData.getEmail());
            sendMailDto.setName(signData.getFirstName());
            sendMailDto.setPassword(signData.getPassword());

            emailService.sendEmail(sendMailDto);

            return "User Account Created Successfully";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "User Account Created, but failed to send email: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }

    private void setCommonUserFields(User user, SignupDto signData, String encodedPassword) {
        user.setFirstName(signData.getFirstName());
        user.setLastName(signData.getLastName());
        user.setEmail(signData.getEmail());
        user.setPassword(encodedPassword);
        user.setRegistrationNumber(signData.getRegistrationNumber());
        user.setDepartment(signData.getDepartment());
        user.setRole(signData.getRole());
    }

    public ResponseEntity<String> updateProfile(int adminId, UpdateAdminProfileDto updateAdminProfileDto) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

        admin.setFirstName(updateAdminProfileDto.getFirstName());
        admin.setLastName(updateAdminProfileDto.getLastName());
        admin.setEmail(updateAdminProfileDto.getEmail());
        admin.setDepartment(updateAdminProfileDto.getDepartment());
        admin.setRegistrationNumber(updateAdminProfileDto.getRegistrationNumber());
        adminRepository.save(admin);

        return ResponseEntity.ok("Admin profile updated successfully");
    }
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !"Admin".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> deleteUser(int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return ResponseEntity.ok("User account deleted successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    public ResponseEntity<String> updateUserProfile(int userId, UpdateProfileDto updateProfileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setFirstName(updateProfileDto.getFirstName());
        user.setLastName(updateProfileDto.getLastName());
        user.setEmail(updateProfileDto.getEmail());
        user.setDepartment(updateProfileDto.getDepartment());
        user.setRegistrationNumber(updateProfileDto.getRegistrationNumber());
        user.setRole(updateProfileDto.getRole());
        userRepository.save(user);

        return ResponseEntity.ok("User profile updated successfully");
    }

    }











