package com.sq022groupA.escalayt.repository;

import com.sq022groupA.escalayt.entity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByResetToken(String token);

    boolean existsByEmail(String email);
}
