package com.example.SpringSecurity.Respositories;

import com.example.SpringSecurity.Entities.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember ,Integer> {
    Optional<TeamMember> findByEmail(String email);

}
