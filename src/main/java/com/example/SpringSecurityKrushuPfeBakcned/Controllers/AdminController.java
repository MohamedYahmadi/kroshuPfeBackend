package com.example.SpringSecurityKrushuPfeBakcned.Controllers;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.*;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.User;
import com.example.SpringSecurityKrushuPfeBakcned.Services.AdminService;
import com.example.SpringSecurityKrushuPfeBakcned.Services.DepartmentService;
import com.example.SpringSecurityKrushuPfeBakcned.Services.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final AdminService adminService;
    private final DepartmentService departmentService;
    private final IndicatorService indicatorService;

    @Autowired
    public AdminController(AdminService adminService, DepartmentService departmentService, IndicatorService indicatorService) {
        this.adminService = adminService;
        this.departmentService = departmentService;
        this.indicatorService = indicatorService;
    }




    @PostMapping("/create-user")
    public String createUser(@RequestBody SignupDto signData) {
        return adminService.createUser(signData);
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id, @RequestBody UpdateAdminProfileDto updateAdminProfileDto) {
        return adminService.updateProfile(id, updateAdminProfileDto);
    }
    @GetMapping("/all-users")
    public List<User>getAllUsers(){
        return adminService.getAllUsers();
    }


    @DeleteMapping("delete-user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id){
        return adminService.deleteUser(id);
    }

    @PutMapping("/update-user-profile/{id}")
    public ResponseEntity<String> updateUserProfile(@PathVariable int id, @RequestBody UpdateProfileDto updateProfileDto) {
        return adminService.updateUserProfile(id, updateProfileDto);
    }
    //Srpint 2 methodes
    @PostMapping("/create-department")
    public String createDepartment(@RequestBody CreateDepartement departmentData) {
        return departmentService.CreateDepartment(departmentData);
    }
    @PostMapping("/create-indicator")
    public ResponseEntity<String> createIndicator(@RequestBody CreateIndicatorDto indicatorDto) {
        return indicatorService.createIndicator(indicatorDto);
    }

    @PutMapping("/set-indicator-value")
    public ResponseEntity<String> setIndicatorValue(@RequestBody SetIndicatorValue indicatorValue) {
        return indicatorService.setIndicatorValue(indicatorValue);
    }

    @GetMapping("/indicators/{departmentId}")
    public List<Indicator> getIndicatorsByDepartment(@PathVariable int departmentId) {
        return indicatorService.getIndicatorsByDepartmentId(departmentId);
    }

    @PutMapping("/update-target")
    public ResponseEntity<String> updateTargetPerWeek(@RequestBody UpdateTargetPerWeekDto updateData) {
        return indicatorService.updateTargetPerWeek(updateData);
    }
    @GetMapping("/indicators-by-department")
    public List<DepartmentIndicatorsDTO> getIndicatorsByDepartment() {
        return indicatorService.categorizeIndicatorsByDepartment();
    }
}


