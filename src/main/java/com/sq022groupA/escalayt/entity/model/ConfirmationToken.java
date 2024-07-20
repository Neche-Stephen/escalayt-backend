package com.sq022groupA.escalayt.entity.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "admin_id")
    private Admin admin;
    /*
    It means that many confirmation tokens can be associated with a single user.For example, a user might request
    multiple confirmation emails, each generating a new confirmation token. This setup ensures that every token can
    be traced back to the user who owns it, which is the foreign key with column name user_id
     */


    public ConfirmationToken(Admin admin){
        this.token = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24); // token valid for 24 hours
        this.admin = admin;
    }

}
