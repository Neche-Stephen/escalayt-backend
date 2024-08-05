package com.sq022groupA.escalayt.repository;

import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.response.AssigneeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByResetToken(String token);

    boolean existsByEmail(String email);

    List<User> findAllByCreatedUnder(Long createdUnderId);



}
