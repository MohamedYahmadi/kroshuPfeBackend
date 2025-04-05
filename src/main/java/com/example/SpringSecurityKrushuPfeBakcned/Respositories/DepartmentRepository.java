package com.example.SpringSecurityKrushuPfeBakcned.Respositories;

import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Department findById(int id );
}
