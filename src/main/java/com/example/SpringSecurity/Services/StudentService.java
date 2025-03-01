package com.example.SpringSecurity.Services;

import com.example.SpringSecurity.Dto.LoginDto;
import com.example.SpringSecurity.Dto.SignupDto;
import com.example.SpringSecurity.Entities.Student;
import com.example.SpringSecurity.Respositories.StudentRepository;
import com.example.SpringSecurity.Respositories.UserRepository;
import com.example.SpringSecurity.Security.JwtService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository    studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;

    public StudentService(UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, JwtService jwtService) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
    }

    public String signUpStudent(SignupDto signData) {
        Optional<Student> newstudent = studentRepository.findByEmail(signData.getEmail());

        if (newstudent.isPresent()){
            throw new IllegalStateException("Email already used");
        }
        String encodedPassword = passwordEncoder.encode(signData.getPassword());

        Student student =new Student(
                signData.getName(),
                signData.getEmail(),
                encodedPassword,
                "DSI"
        );
        studentRepository.save(student);
        return "Account Created successfully";
    }

    public String LoginStudent(LoginDto loginData){
        try {

                authenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(loginData.getEmail(),loginData.getPassword())
                );
                return jwtService.createToken(loginData.getEmail());
        } catch (Exception e) {
            return "Email or password incorrect";
        }
    }
}
