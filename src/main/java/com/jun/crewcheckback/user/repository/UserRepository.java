package com.jun.crewcheckback.user.repository;

import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
