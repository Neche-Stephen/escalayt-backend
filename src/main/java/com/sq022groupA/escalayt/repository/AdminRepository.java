package com.sq022groupA.escalayt.repository;

import com.sq022groupA.escalayt.entity.model.Admin;
import com.sq022groupA.escalayt.entity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByResetToken(String token);

    boolean existsByEmail(String email);

}
