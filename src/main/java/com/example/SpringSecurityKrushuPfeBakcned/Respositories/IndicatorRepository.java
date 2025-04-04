package com.example.SpringSecurityKrushuPfeBakcned.Respositories;

import com.example.SpringSecurityKrushuPfeBakcned.Entities.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndicatorRepository extends JpaRepository<Indicator, Integer> {
    List<Indicator> findByDepartmentId(int departmentId);

}
