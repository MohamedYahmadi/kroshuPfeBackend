package com.example.SpringSecurity.Services;

import com.example.SpringSecurity.Dto.LoginDto;
import com.example.SpringSecurity.Dto.SendMailDto;
import com.example.SpringSecurity.Dto.SignupDto;
import com.example.SpringSecurity.Entities.Admin;
import com.example.SpringSecurity.Entities.TeamMember;
import com.example.SpringSecurity.Entities.User;
import com.example.SpringSecurity.Entities.Viewer;
import com.example.SpringSecurity.Respositories.AdminRepository;
import com.example.SpringSecurity.Respositories.TeamMemberRepository;
import com.example.SpringSecurity.Respositories.UserRepository;
import com.example.SpringSecurity.Respositories.ViewerRepository;
import com.example.SpringSecurity.Security.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

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



    public String loginAdmin(LoginDto loginData) {
        try {
            Admin admin = adminRepository.findByEmail(loginData.getEmail())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            // Verify the password
            if (!passwordEncoder.matches(loginData.getPassword(), admin.getPassword())) {
                throw new BadCredentialsException("Incorrect password");
            }

            // Authenticate the user
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


    public String createUser(SignupDto signData) {
        try {
            // Prevent creating users with the "Admin" role
            if ("Admin".equalsIgnoreCase(signData.getRole())) {
                throw new IllegalStateException("Cannot create a user with the role 'Admin'");
            }

            // Check if the email is already in use
            if (userRepository.findByEmail(signData.getEmail()).isPresent()) {
                throw new IllegalStateException("Email already used");
            }

            // Encode the password
            String encodedPassword = passwordEncoder.encode(signData.getPassword());

            // Save the user in the appropriate table based on their role
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

            // Create a SendMailDto object
            SendMailDto sendMailDto = new SendMailDto();
            sendMailDto.setEmail(signData.getEmail());
            sendMailDto.setName(signData.getFirstName());
            sendMailDto.setPassword(signData.getPassword());

            // Send an email with login details
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

    // Helper method to set common user fields
    private void setCommonUserFields(User user, SignupDto signData, String encodedPassword) {
        user.setFirstName(signData.getFirstName());
        user.setLastName(signData.getLastName());
        user.setEmail(signData.getEmail());
        user.setPassword(encodedPassword);
        user.setRegistrationNumber(signData.getRegistrationNumber());
        user.setDepartment(signData.getDepartment());
        user.setRole(signData.getRole());
    }

    }







