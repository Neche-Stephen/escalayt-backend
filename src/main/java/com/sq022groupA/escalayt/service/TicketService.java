package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.enums.Category;
import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.payload.request.TicketRatingRequest;
import com.sq022groupA.escalayt.payload.request.TicketResolutionRequest;

import java.util.List;

public interface TicketService {

    List<Ticket> filterTickets(Priority priority, Status status, Long assigneeId, Category category);

    Ticket getTicketById(Long ticketId);

    Ticket resolveTicket(Long ticketId, TicketResolutionRequest request);

    Ticket rateTicket(Long ticketId, TicketRatingRequest request);
}
