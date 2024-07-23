package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.entity.model.Admin;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.exception.CustomException;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.service.TokenValidationService;
import com.sq022groupA.escalayt.service.AdminService;
import com.sq022groupA.escalayt.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminService adminService;

    private final TokenValidationService tokenValidationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AdminRequest registrationRequest) {

        try{

            String registerUser  = adminService.register(registrationRequest);

            if(!registerUser.equals("Invalid Email domain")){

                return ResponseEntity.ok("User registered successfully. Please check your email to confirm your account");

            }else {

                return ResponseEntity.badRequest().body("Invalid Email!!!");

            }

        } catch (IllegalArgumentException exception){

            return ResponseEntity.badRequest().body(exception.getMessage());

        } catch (MessagingException e) {

            throw new RuntimeException(e);

        }

    }

    @PostMapping("/login-admin")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto){

        try{

            LoginResponse response = adminService.loginUser(loginRequestDto);
            return ResponseEntity.ok(response);

        }catch (CustomException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token){

        String result = tokenValidationService.validateToken(token);
        if ("Email confirmed successfully".equals(result)) {
            return ResponseEntity.ok(Collections.singletonMap("message", result));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", result));
        }

    }

    // forget password endpoint
    @PostMapping("/initiate-forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordDto forgetPasswordDto){

        String email = forgetPasswordDto.getEmail();
        String result;
        if (userService.existsByEmail(email)) {
            result = userService.forgotPassword(email);
        } else if (adminService.existsByEmail(email)) {
            result = adminService.forgotPassword(email);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user found with this email.");
        }

        //String result = adminService.forgotPassword(forgetPasswordDto);
        return ResponseEntity.ok(Collections.singletonMap("message", result));
    }

    @GetMapping("/confirm-reset-password")
    public ResponseEntity<?> showResetPasswordPage(@RequestParam String token) {
        User user = userService.findByResetToken(token);
        Admin admin = adminService.findByResetToken(token);

        if (user != null) {
            String jwtToken = userService.createToken(user);
            return ResponseEntity.ok("Jwt Token:   " + jwtToken);
        }else if (admin != null) {
            String jwtToken = adminService.createToken(admin);
            return ResponseEntity.ok("Jwt Token:   " + jwtToken);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token.");
        }
    }


//    // new password reset
//    @PostMapping("/new-password-reset")
//    public ResponseEntity<String> newResetPassword(@RequestBody PasswordResetDto request){
//        adminService.resetPassword(request);
//        return ResponseEntity.ok("Password reset successfully. ");
//    }


    // USER/EMPLOYEE RELATED AUTH CONTROLLER \\
    @PostMapping("/login-user")
    public ResponseEntity<?> loginUser1(@RequestBody LoginRequestDto loginRequestDto){
        try{

            LoginResponse response = userService.loginUser(loginRequestDto);
            return ResponseEntity.ok(response);

        }catch (CustomException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }

    }

}
