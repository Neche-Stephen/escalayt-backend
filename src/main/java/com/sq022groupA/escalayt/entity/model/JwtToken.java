package com.sq022groupA.escalayt.entity.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jtoken_tbl")
public class JwtToken extends BaseClass {

    @Column(unique = true)
    public String token;

    public String tokenType = "BEARER";

    public boolean revoked;

    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    public Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
}
