package com.sq022groupA.escalayt.entity.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
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

    private String pictureUrl;

    private LocalDateTime tokenCreationDate;


    @OneToMany(mappedBy = "createdByAdmin")
    @JsonManagedReference
    private List<Ticket> createdTickets;

    @OneToMany(mappedBy = "resolvedByAdmin")
    @JsonManagedReference
    private List<Ticket> resolvedTickets;

    @OneToMany(mappedBy = "assignedByAdmin")
    @JsonManagedReference
    private List<Ticket> assignedTickets;



    @OneToMany(mappedBy = "createdBy")
    @JsonManagedReference
    @JsonIgnoreProperties("createdBy")
    private List<TicketCategory> ticketCategories;

    // the mapping the department
    @OneToMany(mappedBy = "departmentCreatedBy")
    @JsonManagedReference
    @JsonIgnoreProperties("departmentCreatedBy")
    private List<Department> departmentList;




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
    @JsonIgnoreProperties("admin")
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

/*

Extra task:
[11:01 pm] Chinecherem Ubawike
Alright can you do that?
It should take the id of the ticket and and id of the user to be assigned?
then update the ticket by updating the assignee column with the id of the user


 */