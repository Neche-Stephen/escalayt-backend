package com.sq022groupA.escalayt.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminInfo {
    private String userName;

    private String email;

    private String password;
}
