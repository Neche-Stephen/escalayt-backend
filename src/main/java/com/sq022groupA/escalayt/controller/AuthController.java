package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.service.TokenValidationService;
import com.sq022groupA.escalayt.service.AdminService;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequestDto loginRequestDto){

        return ResponseEntity.ok(adminService.loginUser(loginRequestDto));

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
    @PostMapping("/forget_password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordDto forgetPasswordDto){

        String result = adminService.forgotPassword(forgetPasswordDto);

        return ResponseEntity.ok(Collections.singletonMap("message", result));
    }
    
    // new password reset
    @PostMapping("/new-password-reset")
    public ResponseEntity<String> newResetPassword(@RequestBody PasswordResetDto request){
        adminService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully. ");
    }




    // USER/EMPLOYEE RELATED AUTH CONTROLLER \\

    @PostMapping("/login-user")
    public ResponseEntity<LoginResponse> loginUser1(@RequestBody LoginRequestDto loginRequestDto){

        return ResponseEntity.ok(userService.loginUser(loginRequestDto));

    }

}
