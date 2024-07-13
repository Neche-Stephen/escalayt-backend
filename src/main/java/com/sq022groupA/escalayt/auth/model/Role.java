package com.sq022groupA.escalayt.auth.model;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
// removed the entity because it would throw error if i run it
// without a repository interface.........
// i live it for you  my boss.
public class Role {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Long userId;

    private String role;

    @CreationTimestamp
    private LocalDateTime createdAt;


}
