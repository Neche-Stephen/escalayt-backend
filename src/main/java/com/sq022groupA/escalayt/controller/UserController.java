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


}
