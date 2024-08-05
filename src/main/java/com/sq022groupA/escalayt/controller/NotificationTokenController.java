package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.payload.request.NotificationTokenRequest;
import com.sq022groupA.escalayt.payload.request.TokenRequest;
import com.sq022groupA.escalayt.service.NotificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationTokenController {

    @Autowired
    private NotificationTokenService tokenService;

//    @PostMapping("/save-token")
//    public ResponseEntity<String> saveToken(@RequestBody NotificationTokenRequest tokenRequest) {
//        tokenService.saveToken(tokenRequest.getToken());
//        return ResponseEntity.ok("Token saved successfully");
//    }
//
//    @DeleteMapping("/delete-token")
//    public ResponseEntity<String> deleteToken(@RequestBody NotificationTokenRequest tokenRequest) {
//        tokenService.deleteToken(tokenRequest.getToken());
//        return ResponseEntity.ok("Token deleted successfully");
//    }

    @PostMapping("/save")
    public ResponseEntity<String> saveToken(@RequestBody TokenRequest tokenRequest) {
        try {
            boolean tokenExists = tokenService.tokenExists(tokenRequest.getToken());
            if (tokenExists) {
                return ResponseEntity.status(409).body("Token already exists.");
            } else {
                tokenService.saveToken(tokenRequest.getUserId(), tokenRequest.getToken());
                return ResponseEntity.ok("Token saved successfully.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to save token: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteToken(@RequestBody TokenRequest tokenRequest) {
        try {
            tokenService.deleteToken(tokenRequest.getUserId(), tokenRequest.getToken());
            return ResponseEntity.ok("Token deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete token: " + e.getMessage());
        }
    }
}
