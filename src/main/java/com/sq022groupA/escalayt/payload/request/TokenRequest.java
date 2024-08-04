package com.sq022groupA.escalayt.payload.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class TokenRequest {
    private Long userId;
    private String token;
}
