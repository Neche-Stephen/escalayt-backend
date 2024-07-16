package com.sq022groupA.escalayt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordResetDto {

    private String email;
    private String newPassword;
}
