package com.sq022groupA.escalayt.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    private String title;
    private String body;
    private String topic;
    private String token;
}
