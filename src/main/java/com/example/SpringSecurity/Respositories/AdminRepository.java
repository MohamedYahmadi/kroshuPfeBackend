package com.example.SpringSecurity.Respositories;

import com.example.SpringSecurity.Entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

   Optional <Admin> findByEmail(String email);
}
