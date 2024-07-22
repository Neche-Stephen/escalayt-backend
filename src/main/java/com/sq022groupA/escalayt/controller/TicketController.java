package com.sq022groupA.escalayt.controller;


import com.sq022groupA.escalayt.payload.response.TicketCountResponse;
import com.sq022groupA.escalayt.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket")
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/get")
    public String helloWorld(){
        return "Hello World!!!";
    }

    @GetMapping("/admin-count/{adminId}")
    public ResponseEntity<TicketCountResponse> getAdminTicketCount(@PathVariable Long adminId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        TicketCountResponse response = ticketService.getAdminTicketCount(adminId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-count/{userId}")
    public ResponseEntity<TicketCountResponse> getUserTicketCount(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        TicketCountResponse response = ticketService.getUserTicketCount(userId);
        return ResponseEntity.ok(response);
    }
}
