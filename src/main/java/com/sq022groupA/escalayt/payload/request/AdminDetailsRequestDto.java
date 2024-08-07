package com.sq022groupA.escalayt.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDetailsRequestDto {


    private String firstName;


    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;
}
