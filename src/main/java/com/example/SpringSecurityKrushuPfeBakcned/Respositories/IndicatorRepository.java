package com.example.SpringSecurityKrushuPfeBakcned.Respositories;

import com.example.SpringSecurityKrushuPfeBakcned.Entities.Department;
import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndicatorRepository extends JpaRepository<Indicator, Integer> {
    void deleteByDepartmentId(int departmentId);
    List<Indicator> findByDepartmentId(int departmentId);
    @Query("SELECT i FROM Indicator i LEFT JOIN FETCH i.dailyValues WHERE i.id IN :ids")
    List<Indicator> findIndicatorsWithDailyValues(@Param("ids") List<Integer> indicatorIds);
    Optional<Indicator> findByNameAndDepartment(String name, Department department);


}
