package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.payload.request.UserRequest;
import com.sq022groupA.escalayt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/edit/{userId}")
    public ResponseEntity<String> editUserDetails(@PathVariable Long userId, @RequestBody UserRequest userRequest){
        String response = userService.editUserDetails(userId, userRequest);
        return ResponseEntity.ok(response);
    }
}
