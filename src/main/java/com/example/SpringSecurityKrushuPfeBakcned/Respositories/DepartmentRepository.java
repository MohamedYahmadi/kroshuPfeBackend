package com.example.SpringSecurityKrushuPfeBakcned.Respositories;

import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.indicators")
    List<Department> findAllWithIndicators();


    boolean existsByName(String newName);
}
