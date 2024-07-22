package com.sq022groupA.escalayt.repository;

import com.sq022groupA.escalayt.entity.model.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    void deleteByToken(String token);
    NotificationToken findByToken(String token);
}
