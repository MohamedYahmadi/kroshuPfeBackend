package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.CreateDepartement;
import com.example.SpringSecurityKrushuPfeBakcned.Dto.RenameDepartmentDto;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.DepartmentRepository;
import com.example.SpringSecurityKrushuPfeBakcned.Respositories.IndicatorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final IndicatorRepository indicatorRepository;

    public DepartmentService(DepartmentRepository departmentRepository, IndicatorRepository indicatorRepository) {
        this.departmentRepository = departmentRepository;
        this.indicatorRepository = indicatorRepository;
    }

    public String CreateDepartment(CreateDepartement departmentData) {
        Department newDepartment = Department.builder()
                .name(departmentData.getName())
                .build();

        departmentRepository.save(newDepartment);

        return "Department created successfully";
    }
    public List<String> getAllDepartmentNames() {
        return departmentRepository.findAll()
                .stream()
                .map(Department::getName)
                .collect(Collectors.toList());
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public ResponseEntity<String> deleteDepartment(int departmentId) {
        if (departmentRepository.existsById(departmentId)) {
            indicatorRepository.deleteByDepartmentId(departmentId);
            departmentRepository.deleteById(departmentId);
            return ResponseEntity.ok("Department and related indicators deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Department not found");
        }
    }
    public ResponseEntity<String> renameDepartment(int departmentId, RenameDepartmentDto renameDepartmentDto) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + departmentId));

        String newName = renameDepartmentDto.getNewName();

        if (department.getName().equals(newName)) {
            return ResponseEntity.badRequest().body("New name is the same as current name");
        }

        if (departmentRepository.existsByName(newName)) {
            return ResponseEntity.badRequest().body("Department name already exists");
        }

        department.setName(newName);
        departmentRepository.save(department);

        return ResponseEntity.ok("Department renamed successfully");
    }
}





