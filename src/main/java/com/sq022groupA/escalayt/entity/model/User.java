package com.sq022groupA.escalayt.entity.model;

import com.sq022groupA.escalayt.auth.model.JwtToken;
import com.sq022groupA.escalayt.entity.enums.Role;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user_tbl")
public class User extends BaseClass implements UserDetails {

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String password;

    private String phoneNumber;



    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    // implemented a soft so upon delete of the user
    // status changes to false instead of delete.
    private boolean isActive = true; // Flag for soft delete

    // this is the update
    @OneToMany(mappedBy = "createdBy")
    private List<Ticket> createdTickets;

    @OneToMany(mappedBy = "resolvedBy")
    private List<Ticket> resolvedTickets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JwtToken> jtokens;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }




}
