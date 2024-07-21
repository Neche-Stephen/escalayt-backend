package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.exception.ErrorResponse;
import com.sq022groupA.escalayt.payload.request.UserDetailsDto;
import com.sq022groupA.escalayt.payload.request.UserRegistrationDto;
import com.sq022groupA.escalayt.payload.response.UserRegistrationResponse;
import com.sq022groupA.escalayt.service.AdminService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Endpoint to handle PUT requests for editing user details.
    @PutMapping("/edit")
    public ResponseEntity<String> editUserDetails(@RequestBody UserDetailsDto userDetailsDto){

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Delegate user details editing to UserService and get response.
        String response = adminService.editUserDetails(currentUsername, userDetailsDto);

        // Return HTTP 200 OK response with the edited user details response.
        return ResponseEntity.ok(response);
    }



    /////------ USER/EMPLOYEE RELATED ADMIN ENDPOINTS -----\\\\\

    @PostMapping("/register-user")
    public ResponseEntity<UserRegistrationResponse> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        UserRegistrationResponse response = adminService.registerUser(currentUsername, userRegistrationDto);
        return ResponseEntity.ok(response);
    }
}