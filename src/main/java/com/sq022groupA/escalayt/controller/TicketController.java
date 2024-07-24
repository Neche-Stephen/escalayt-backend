package com.sq022groupA.escalayt.controller;


import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.entity.model.TicketComment;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.TicketCategoryResponseDto;
import com.sq022groupA.escalayt.payload.response.TicketCommentResponse;
import com.sq022groupA.escalayt.payload.response.TicketCountResponse;
import com.sq022groupA.escalayt.payload.response.TicketResponseDto;
import com.sq022groupA.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


    @GetMapping("/category/ticket/{id}/get-comments")
    public ResponseEntity<?> ticketComment(@PathVariable Long id){


        // get the list of the comments
        List<TicketComment> response = ticketService.getTicketComments(id);


        // return the response
        return ResponseEntity.ok(response);
    }


    // create a new comment
    @PostMapping("/category/ticket/{id}/create-comment")
    public ResponseEntity<?> createComment(@PathVariable Long id, @RequestBody TicketCommentRequestDto ticketCommentRequestDto){

        // Get the currently authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // update the db and return response
        TicketCommentResponse ticketCommentResponse = ticketService.createTicketComment(ticketCommentRequestDto, id, currentUsername);


        return ResponseEntity.ok(ticketCommentResponse);
    }


    //count tickets
    @GetMapping("/count")
    public ResponseEntity<TicketCountResponse> getTicketCount() {
        // Get the currently authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        TicketCountResponse response = ticketService.getTicketCountByUsername(currentUsername);

        return ResponseEntity.ok(response);
    }



    // create ticket category
    @PostMapping("/category/create")
    public ResponseEntity<?> createTicketCategory(@RequestBody TicketCategoryRequestDto ticketCategoryRequest){

        // Get the currently authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        TicketCategoryResponseDto response = ticketService.createTicketCategory(ticketCategoryRequest, currentUsername);

        return ResponseEntity.ok(response);
    }

    // get tickets by category
    @GetMapping("/category/{id}")
    public ResponseEntity<?> getTicketsByCat(@PathVariable Long id){


        // get the list of the comments
        List<Ticket> response = ticketService.getTicketByCategory(id);


        // return the response
        return ResponseEntity.ok(response);
    }


    // create ticket category
    @PostMapping("/category/{id}/ticket/create-ticket")
    public ResponseEntity<?> createTicket(@PathVariable Long id , @RequestBody TicketRequestDto ticketRequestDto){

        // get the user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // create new ticket
        TicketResponseDto response = ticketService.createTicket(id, ticketRequestDto, currentUsername);

        return ResponseEntity.ok(response);
    }

    // delete the ticket rightly
    @DeleteMapping("/category/ticket/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id){
        TicketResponseDto response = ticketService.deleteTicket(id);
        return ResponseEntity.ok(response);
    }

    // Endpoint to get the latest 3 open tickets for only admin
    @GetMapping("/admin/open-tickets")
    public ResponseEntity<?> getLatestThreeOpenTickets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        List<Ticket> openTickets = ticketService.getLatestThreeOpenTickets(currentUsername);
        return ResponseEntity.ok(openTickets);
    }

    // filter ticket
    @GetMapping("/filter")
    public ResponseEntity<List<Ticket>> filterTickets(@RequestParam(required = false) Priority priority,
                                                      @RequestParam(required = false) Status status,
                                                      @RequestParam(required = false) Long assigneeId,
                                                      @RequestParam(required = false) Long categoryId) {

        List<Ticket> tickets = ticketService.filterTickets(priority, status, assigneeId, categoryId);
        return ResponseEntity.ok(tickets);
    }

    // preview a ticket
    @GetMapping("/preview-ticket/{ticketId}")
    public ResponseEntity<Ticket> previewTicket(@PathVariable Long ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{ticketId}/resolve")
    public ResponseEntity<Ticket> resolveTicket(@PathVariable Long ticketId,
            @RequestBody TicketResolutionRequest resolutionRequest) {

        Ticket resolvedTicket = ticketService.resolveTicket(ticketId, resolutionRequest);
        return ResponseEntity.ok(resolvedTicket);
    }


}
