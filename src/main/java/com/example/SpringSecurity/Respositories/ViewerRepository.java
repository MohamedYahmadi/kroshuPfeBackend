package com.example.SpringSecurity.Respositories;

import com.example.SpringSecurity.Entities.Viewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViewerRepository extends JpaRepository<Viewer ,Integer> {
    Optional<Viewer> findByEmail(String email);

}
