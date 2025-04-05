package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.CreateDepartement;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.DepartmentRepository;
import org.springframework.stereotype.Service;

@Service

public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public String CreateDepartment(CreateDepartement departmentData) {
        Department newDepartment = Department.builder()
                .name(departmentData.getName())
                .build();

        departmentRepository.save(newDepartment);

        return "Department created successfully";
    }
}
