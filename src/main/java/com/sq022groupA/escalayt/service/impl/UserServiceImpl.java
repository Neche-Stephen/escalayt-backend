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
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManagerUser;
    private final JwtService jwtService;
    private final JwtTokenRepository jwtTokenRepository;

    @Value("${baseUrl}")
    private String baseUrl;

    // USER/EMPLOYEE REGISTRATION
    @Override
    public UserRegistrationResponse registerUser(String currentUsername, UserRegistrationDto userRegistrationDto) throws MessagingException {
        // GET ADMIN ID BY USERNAME
        Optional<Admin> loggedInAdmin = adminRepository.findByUsername(currentUsername);

        // Check if admin is present
        if (loggedInAdmin.isEmpty()) {
            throw new RuntimeException("Admin user not found");
        }

        // Set the loggedInUser.get().getId() into the userRegistrationDto
        //userRegistrationDto.setCreatedUnder(loggedInAdmin.get().getId());

        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDto.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User email already exists");
        }

        Optional<Role> userRole = roleRepository.findByName("USER");
        if (userRole.isEmpty()) {
            throw new RuntimeException("Default role ADMIN not found in the database.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole.get());

        // Build new User entity
        User newUser = User.builder()
                .fullName(userRegistrationDto.getFullName())
                .email(userRegistrationDto.getEmail())
                .phoneNumber(userRegistrationDto.getPhoneNumber())
                .jobTitle(userRegistrationDto.getJobTitle())
                .department(userRegistrationDto.getDepartment())
                .username(userRegistrationDto.getUsername())
                .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
                .createdUnder(loggedInAdmin.get().getId())
                .roles(roles)
                .build();

        // Save new user to the repository
        User savedUser = userRepository.save(newUser);

        // Set up email message for the registered user/employee
        String userLoginUrl = baseUrl + "/user-login";

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACTIVATE YOUR ACCOUNT")
                .messageBody(UserRegistrationEmailBody.buildEmail(savedUser.getFullName(),
                        savedUser.getUsername(), userRegistrationDto.getPassword(), userLoginUrl))
                .build();

        // Send email message to the registered user/employee
        emailService.mimeMailMessage(emailDetails);

        // Method response
        return UserRegistrationResponse.builder()
                .responseTemplate(ResponseTemplate.builder()
                        .responseCode("007")
                        .responseMessage("User/Employee Created Successfully")
                        .build())
                .fullName(savedUser.getFullName())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .jobTitle(savedUser.getJobTitle())
                .department(savedUser.getDepartment())
                .createdUnder(savedUser.getCreatedUnder())
                .build();
    }

    @Override
    public LoginResponse loginUser(LoginRequestDto loginRequestDto) {
//        authenticationManagerUser.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequestDto.getUsername(),
//                        loginRequestDto.getPassword()
//                )
//        );
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
