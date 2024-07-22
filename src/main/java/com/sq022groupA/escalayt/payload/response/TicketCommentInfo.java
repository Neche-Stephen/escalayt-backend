package com.sq022groupA.escalayt.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketCommentInfo {

    // ticket id
    private String ticketTitle;

    // the comment
    private String comment;

    // the person that made the comment
    private String commenter;

    // when the comment was made
    private LocalDateTime createdAt;

}
