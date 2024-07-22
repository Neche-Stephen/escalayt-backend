package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.entity.model.TicketComment;
import com.sq022groupA.escalayt.payload.request.TicketCategoryRequestDto;
import com.sq022groupA.escalayt.payload.request.TicketCommentRequestDto;
import com.sq022groupA.escalayt.payload.request.TicketRequestDto;
import com.sq022groupA.escalayt.payload.response.TicketCategoryResponseDto;
import com.sq022groupA.escalayt.payload.response.TicketCommentResponse;
import com.sq022groupA.escalayt.payload.response.TicketInfo;
import com.sq022groupA.escalayt.payload.response.TicketResponseDto;

import java.util.List;

public interface TicketService {

    // create the comment
    TicketCommentResponse createTicketComment(TicketCommentRequestDto commentRequestDto, Long ticketId, String commenter);

    // get all the comment made under a particular ticket
    List<TicketComment> getTicketComments(Long ticketId);

    // create category

    TicketCategoryResponseDto createTicketCategory(TicketCategoryRequestDto ticketCategoryRequest, String username);

    // this is to get ticket by category id
    List<Ticket> getTicketByCategory(Long id);

    // create new ticket
    TicketResponseDto createTicket(Long catId, TicketRequestDto ticketRequest, String username);

    // delete ticket
    TicketResponseDto deleteTicket(Long ticketId);


}
