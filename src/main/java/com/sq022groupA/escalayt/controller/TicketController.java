package com.sq022groupA.escalayt.controller;


import com.sq022groupA.escalayt.entity.enums.Category;
import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.payload.request.TicketRatingRequest;
import com.sq022groupA.escalayt.payload.request.TicketResolutionRequest;
import com.sq022groupA.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/get")
    public String helloWorld(){
        return "Hello World!!!";
    }

    //Endpoint to filter tickets by priority, status, assignee, category
    @GetMapping("/filter")
    public ResponseEntity<List<Ticket>> filterTickets(@RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status, @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Category category) {

        List<Ticket> filteredTickets = ticketService.filterTickets(priority, status, assigneeId, category);
        return ResponseEntity.ok(filteredTickets);
    }

    //Endpoint to preview a ticket
    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    //Endpoint to resolve a ticket
    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<Ticket> resolveTicket(@PathVariable Long ticketId,
                                                @RequestBody TicketResolutionRequest request) {

        Ticket resolvedTicket = ticketService.resolveTicket(ticketId, request);
        return ResponseEntity.ok(resolvedTicket);
    }

    //Endpoint to rate ticket resolution
    @PostMapping("/{ticketId}/rate")
    public ResponseEntity<Ticket> rateTicketResolution(@PathVariable Long ticketId,
                                                       @RequestBody TicketRatingRequest request) {

        Ticket ratedTicket = ticketService.rateTicket(ticketId, request);
        return ResponseEntity.ok(ratedTicket);
    }
}
