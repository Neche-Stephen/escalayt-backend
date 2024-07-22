package com.sq022groupA.escalayt.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketCategoryResponseDto {

    private String responseMessage;

    private String responseCode;

    private TicketCategoryInfo ticketCategoryInfo;
}
