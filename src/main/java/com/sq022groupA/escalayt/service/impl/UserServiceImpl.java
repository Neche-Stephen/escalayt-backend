package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.auth.model.ConfirmationToken;
import com.sq022groupA.escalayt.auth.model.JwtToken;
import com.sq022groupA.escalayt.auth.model.Role;
import com.sq022groupA.escalayt.auth.repository.ConfirmationTokenRepository;
import com.sq022groupA.escalayt.auth.repository.JwtTokenRepository;
import com.sq022groupA.escalayt.auth.repository.RoleRepository;
import com.sq022groupA.escalayt.auth.service.JwtService;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.request.LoginRequestDto;
import com.sq022groupA.escalayt.payload.request.UserDetailsDto;
import com.sq022groupA.escalayt.payload.request.UserRequest;
import com.sq022groupA.escalayt.payload.response.EmailDetails;
import com.sq022groupA.escalayt.payload.response.LoginInfo;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.repository.UserRepository;
import com.sq022groupA.escalayt.service.EmailService;
import com.sq022groupA.escalayt.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenRepository jwtTokenRepository ;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${baseUrl}")
    private String baseUrl;


    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Generate password reset token and send email
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setTokenCreationDate(LocalDateTime.now());
        userRepository.save(user);

        // Send email with reset token (pseudo-code)
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token."));

        if (user.getTokenCreationDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new RuntimeException("Token has expired.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenCreationDate(null);
        userRepository.save(user);
    }

    public void editUserDetails(Long id, UserDetailsDto userDetailsDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(userDetailsDto.getFirstName());
        user.setLastName(userDetailsDto.getLastName());
        user.setPhoneNumber(userDetailsDto.getPhoneNumber());
        userRepository.save(user);
    }


}
