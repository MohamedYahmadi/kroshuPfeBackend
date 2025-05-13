package com.example.SpringSecurityKrushuPfeBakcned.Respositories;

import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.WasteReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WasteReasonRepository extends JpaRepository<WasteReason, Integer> {

    List<WasteReason> findByDepartment(Department department);

    @Query("SELECT wr FROM WasteReason wr WHERE wr.department.name = :departmentName ORDER BY wr.createdAt DESC")
    List<WasteReason> findByDepartmentName(@Param("departmentName") String departmentName);

    long countByDepartment(Department department);

    @Query("SELECT wr FROM WasteReason wr WHERE wr.department = :department AND wr.createdAt >= :since ORDER BY wr.createdAt DESC")
    List<WasteReason> findRecentByDepartment(
            @Param("department") Department department,
            @Param("since") LocalDateTime since
    );

    List<WasteReason> findByCreatedBy_Id(Integer userId);

    @Query("SELECT wr FROM WasteReason wr WHERE wr.department = :department AND LOWER(wr.reason) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<WasteReason> searchByDepartmentAndReason(
            @Param("department") Department department,
            @Param("searchTerm") String searchTerm
    );
}