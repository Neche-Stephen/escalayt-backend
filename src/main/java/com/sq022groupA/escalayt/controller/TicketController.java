package com.sq022groupA.escalayt.controller;


import com.sq022groupA.escalayt.entity.enums.Priority;
import com.sq022groupA.escalayt.entity.enums.Status;
import com.sq022groupA.escalayt.entity.model.Admin;
import com.sq022groupA.escalayt.entity.model.Ticket;
import com.sq022groupA.escalayt.entity.model.TicketComment;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.*;
import com.sq022groupA.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // get category name
    @GetMapping("/category/name")
    public List<String> getCategoryName(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return ticketService.getCategoryName(currentUsername);
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

        List<TicketDto> openTickets = ticketService.getLatestThreeOpenTickets(currentUsername);
        return ResponseEntity.ok(openTickets);
    }

    // Endpoint to get the latest 3 resolved tickets for only admin
    @GetMapping("/admin/resolved-tickets")
    public ResponseEntity<?> getLatestThreeResolvedTickets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        List<TicketDto> resolvedTickets = ticketService.getLatestThreeResolvedTickets(currentUsername);
        return ResponseEntity.ok(resolvedTickets);
    }

    // Endpoint to get the latest 3 resolved tickets for only admin
    @GetMapping("/admin/inprogres-tickets")
    public ResponseEntity<?> getLatestThreeInprogressTickets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        List<TicketDto> resolvedTickets = ticketService.getLatestThreeInprogressTickets(currentUsername);
        return ResponseEntity.ok(resolvedTickets);
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

    // Filter tickets with pagination
    @GetMapping("/filter-new")
    public ResponseEntity<Page<TicketResponse>> filterTicketsWithPagination(
            @RequestParam(required = false) List<Priority> priority,
            @RequestParam(required = false) List<Status> status,
            @RequestParam(required = false) List<Long> assigneeId,
            @RequestParam(required = false) List<Long> categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {

        Page<TicketResponse> tickets = ticketService.filterTicketsWithPagination(priority, status, assigneeId, categoryId, page, size);
        return ResponseEntity.ok(tickets);
    }


    // preview a ticket
    @GetMapping("/preview-ticket/{ticketId}")
    public ResponseEntity<Ticket> previewTicket(@PathVariable Long ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    // view all tickets
    @GetMapping("/view-all-tickets")
    public List<TicketResponse> viewAllTickets(@RequestParam(defaultValue = "0") int page){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        int size = 14;

        return ticketService.getAllTicket(currentUsername, page, size);
    }


    @PostMapping("/{ticketId}/resolve")
    public ResponseEntity<String> resolveTicket(@PathVariable Long ticketId) {
        // Get the currently authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Resolve the ticket using the service
        ticketService.resolveTicket(ticketId, currentUsername);

        return ResponseEntity.ok("Ticket resolved successfully");
    }


    @PostMapping("/{ticketId}/rate")
    public ResponseEntity<?> rateTicket(@PathVariable Long ticketId,
                                             @RequestBody TicketRatingRequest ratingRequest) {

        ticketService.rateTicket(ticketId, ratingRequest);
        return ResponseEntity.ok().build();
    }

    //endpoint to get all recent activities
    @GetMapping("/all-recent-activities")
    public ResponseEntity<Page<TicketActivitiesResponseDto>>listAllRecentTicketActivities(
            @RequestParam(defaultValue = "0") int page){

        // get the user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        String role = "";
        Long id = null;

        Admin admin = ticketService.getAdminId(currentUsername);
        User user = ticketService.getUserId(currentUsername);

        if(admin != null ){
            role = "ADMIN";
            id = admin.getId();
        }else{
            role = "USER";
            id = user.getId();
        }

        Pageable pageable = PageRequest.of(page, 7);
        //Get the authorities (roles) of the current user
        Page<TicketActivitiesResponseDto> recentTickets = ticketService.listAllRecentTicketActivities(id, role, pageable);

        return ResponseEntity.ok(recentTickets);
    }

    @PutMapping("/assign-ticket/{id}")
    public ResponseEntity<String> assignTicket(@PathVariable Long id, @RequestBody Long assignId){

        String response = ticketService.assignTicket(id, assignId);
        return ResponseEntity.ok(response);
    }
}
