package com.sq022groupA.escalayt.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TicketCommentDTO {
    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    private String fullName;
    private String profileUrl;

}
