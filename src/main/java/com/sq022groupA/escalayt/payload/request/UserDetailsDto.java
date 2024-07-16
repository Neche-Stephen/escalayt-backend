package com.sq022groupA.escalayt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;
}
