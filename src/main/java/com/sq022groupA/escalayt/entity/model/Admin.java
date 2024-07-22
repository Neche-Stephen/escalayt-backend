package com.sq022groupA.escalayt.entity.model;

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
@Table(name = "admin_tbl")
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String password;

    private String phoneNumber;

    private boolean enabled;

    private boolean isActive = true; // Flag for soft delete

    private String resetToken;

    private LocalDateTime tokenCreationDate;


    @OneToMany(mappedBy = "createdByAdmin")
    private List<Ticket> createdTickets;

    @OneToMany(mappedBy = "resolvedByAdmin")
    private List<Ticket> resolvedTickets;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_roles",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // to establish a relationship btw the admin and user
    @OneToMany(mappedBy = "admin")
    private List<User> users;



    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
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


    @Override
    public boolean isEnabled() {
        return enabled;
    };
}
