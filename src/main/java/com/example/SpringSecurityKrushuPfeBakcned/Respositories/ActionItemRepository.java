package com.example.SpringSecurityKrushuPfeBakcned.Respositories;

import com.example.SpringSecurityKrushuPfeBakcned.Entities.ActionItem;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActionItemRepository extends JpaRepository<ActionItem, Integer> {


    List<ActionItem> findByDepartmentOrderByCreatedAtDesc(Department department);


    @Query("SELECT ai FROM ActionItem ai WHERE ai.department.name = :departmentName ORDER BY ai.createdAt DESC")
    List<ActionItem> findByDepartmentNameOrderByCreatedAtDesc(@Param("departmentName") String departmentName);


    long countByDepartment(Department department);


    @Query("SELECT ai FROM ActionItem ai WHERE ai.department = :department AND ai.createdAt >= :since ORDER BY ai.createdAt DESC")
    List<ActionItem> findRecentByDepartment(
            @Param("department") Department department,
            @Param("since") LocalDateTime since
    );
}