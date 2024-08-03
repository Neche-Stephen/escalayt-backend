package com.sq022groupA.escalayt.payload.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeDTO {

    private String fullName;
    private String email;
    private String jobTitle;
    private String phoneNumber;

    public AssigneeDTO(String fullName, String jobTitle) {
        this.fullName = fullName;

        this.jobTitle = jobTitle;
    }
}
