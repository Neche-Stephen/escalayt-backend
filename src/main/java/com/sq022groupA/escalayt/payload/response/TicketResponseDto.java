package com.sq022groupA.escalayt.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDto {


    private String responseCode;

    private String responseMessage;

    private TicketInfo ticketInfo;
}
