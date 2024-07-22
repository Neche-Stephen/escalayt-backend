package com.sq022groupA.escalayt.controller;


import com.sq022groupA.escalayt.entity.model.TicketComment;
import com.sq022groupA.escalayt.payload.request.TicketCommentRequestDto;
import com.sq022groupA.escalayt.payload.response.TicketCommentResponse;
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

//    @GetMapping("/get")
//    public String helloWorld(){
//        return "Hello World!!!";
//    }

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
}
