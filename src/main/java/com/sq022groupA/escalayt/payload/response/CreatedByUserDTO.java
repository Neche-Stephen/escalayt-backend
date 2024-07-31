package com.sq022groupA.escalayt.payload.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedByUserDTO {

    private String fullName;
    private String email;
    private String jobTitle;
    private String department;
    private String phoneNumber;
    private String pictureUrl;
    private String username;

}