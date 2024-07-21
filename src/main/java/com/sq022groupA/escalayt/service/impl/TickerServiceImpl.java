package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.enums.Category;
import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.exception.TicketNotFoundException;
import com.sq022groupA.escalayt.payload.request.TicketRatingRequest;
import com.sq022groupA.escalayt.payload.request.TicketResolutionRequest;
import com.sq022groupA.escalayt.repository.TicketRepository;
import com.sq022groupA.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TickerServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;


    public List<Ticket> filterTickets(Priority priority, Status status, Long assigneeId, Category category) {
        // filter the ticket based on the parameter passed in
        return ticketRepository.findByFilters(priority, status, assigneeId, category);
    }


    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id " + ticketId));
    }


    public Ticket resolveTicket(Long ticketId, TicketResolutionRequest request) {

        Ticket ticket = getTicketById(ticketId);
        ticket.setStatus(Status.RESOLVE);
        ticket.setResolvedByUser(request.getResolvedByUser());
        ticket.setResolvedByAdmin(request.getResolvedByAdmin());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);

    }


    public Ticket rateTicket(Long ticketId, TicketRatingRequest request) {
        Ticket ticket = getTicketById(ticketId);
        ticket.setRating(request.getRating());
        ticket.setReview(request.getReview());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

}
