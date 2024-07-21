package com.sq022groupA.escalayt.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "notification_token_tbl")
public class NotificationToken extends BaseClass {
    @Column(unique = true)
    private String token;
}
