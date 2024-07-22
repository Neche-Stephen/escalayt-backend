package com.sq022groupA.escalayt.payload.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketCommentResponse {


    private String responseCode;

    private String responseMessage;

    private TicketCommentInfo ticketCommentInfo;


}
