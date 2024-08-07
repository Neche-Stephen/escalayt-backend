package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.payload.response.AdminUserDetailsDto;
import com.sq022groupA.escalayt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/new-user-password")
    public ResponseEntity<?> resetPassword(@RequestParam String newPassword) {

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        userService.updatePassword(currentUsername, newPassword);

        return ResponseEntity.ok("Password reset successfully.");
    }

    @GetMapping("/get-user-detail")
    public ResponseEntity<AdminUserDetailsDto> getUserDetails(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        return ResponseEntity.ok(userService.getUserDetails(currentUsername));

    }

    // edit user detail here
    @PutMapping("/edit-user-detail")
    public ResponseEntity<AdminUserDetailsDto> editUserDetails(@RequestBody AdminUserDetailsDto adminUserDetailsDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();



        return ResponseEntity.ok(userService.editUserInfo(currentUsername, adminUserDetailsDto));

    }

}