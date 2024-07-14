package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.service.TokenValidationService;
import com.sq022groupA.escalayt.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final TokenValidationService tokenValidationService;


    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token){

        String result = tokenValidationService.validateToken(token);
        if ("Email confirmed successfully".equals(result)) {
            return ResponseEntity.ok(Collections.singletonMap("message", result));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", result));
        }

    }

    @PostMapping("/password-reset/initiate")
    public ResponseEntity<String> initiatePasswordReset(@RequestBody PasswordResetRequestDto request) {
        userService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> editUserDetails(@PathVariable Long id, @RequestBody UserDetailsDto userDetailsDto) {
        userService.editUserDetails(id, userDetailsDto);
        return ResponseEntity.ok("User details updated successfully.");
    }
}
