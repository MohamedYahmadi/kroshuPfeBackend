package com.example.SpringSecurity.Controllers;

import com.example.SpringSecurity.Dto.LoginDto;
import com.example.SpringSecurity.Dto.SignupDto;
import com.example.SpringSecurity.Services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/student")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @PostMapping("/Signup")
    public String signUpStudent(@RequestBody SignupDto signData) {

        return studentService.signUpStudent(signData);
    }
    @PostMapping("/Login")
    public String LoginStudent(@RequestBody LoginDto loginData) {
        return studentService.LoginStudent(loginData);
    }
        @PostMapping("/reset-password")
        public String resetPassword() {
            return "Reset Password";
        }

        @PostMapping("create")
        public String createStudent() {
            return "Protected route";
        }

    }

