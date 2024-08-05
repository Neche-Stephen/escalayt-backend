package com.sq022groupA.escalayt.entity.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user_tbl")
public class User extends BaseClass implements UserDetails {

    private String fullName;

    private String username;

    private String userImg;

    private String email;

    private String password;

    private String phoneNumber;

    private String jobTitle;

    private String pictureUrl;

    //private String department;

    private Long createdUnder; // Id of the Admin

    private boolean isActive = true; // Flag for soft delete

    private String resetToken;

    private LocalDateTime tokenCreationDate;


    @OneToMany(mappedBy = "createdByUser")
    @JsonManagedReference
    private List<Ticket> createdTickets;


    @OneToMany(mappedBy = "resolvedByUser")
    @JsonManagedReference
    private List<Ticket> resolvedTickets;

    @OneToMany(mappedBy = "commenter")
    @JsonManagedReference
    private List<TicketComment> comments;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    //a relationship between the employees and admin
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private Admin admin;


    // mapped user to department
    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonBackReference
    private Department employeeDepartment;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<JwtToken> jtokens;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

}
