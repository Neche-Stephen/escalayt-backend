package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.config.JwtService;
import com.sq022groupA.escalayt.entity.model.*;
import com.sq022groupA.escalayt.payload.response.*;
import com.sq022groupA.escalayt.repository.*;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.service.EmailService;
import com.sq022groupA.escalayt.service.UserService;
import com.sq022groupA.escalayt.utils.UserRegistrationEmailBody;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtTokenRepository jwtTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Value("${baseUrl}")
    private String baseUrl;

    @Override
    public LoginResponse loginUser(LoginRequestDto loginRequestDto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + loginRequestDto.getUsername()));

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return LoginResponse.builder()
                .responseCode("002")
                .responseMessage("Login Successfully")
                .loginInfo(LoginInfo.builder()
                        .username(user.getUsername())
                        .token(jwtToken)
                        .build())
                .build();
    }

    private void saveUserToken(User userModel, String jwtToken) {
        var token = JwtToken.builder()
                .user(userModel)
                .token(jwtToken)
                .tokenType("BEARER")
                .expired(false)
                .revoked(false)
                .build();
        jwtTokenRepository.save(token);
    }

    private void revokeAllUserTokens(User userModel) {
        var validUserTokens = jwtTokenRepository.findAllValidTokenByUser(userModel.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jwtTokenRepository.saveAll(validUserTokens);
    }
}
