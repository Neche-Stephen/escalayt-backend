package com.sq022groupA.escalayt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  UserRegistrationResponse {
    private ResponseTemplate responseTemplate;

    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private String jobTitle;
    private String department;
    private long createdUnder;

}
