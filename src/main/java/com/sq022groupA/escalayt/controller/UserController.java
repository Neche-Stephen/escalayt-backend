package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.payload.request.LoginRequestDto;
import com.sq022groupA.escalayt.payload.request.UserRegistrationDto;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.payload.response.UserRegistrationResponse;
import com.sq022groupA.escalayt.service.AdminService;
import com.sq022groupA.escalayt.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // @PreAuthorize("ROLE_ADMIN")
    @PostMapping("/register-user")
    public ResponseEntity<UserRegistrationResponse> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) throws MessagingException {

        return ResponseEntity.ok(userService.registerUser(userRegistrationDto));

    }
}