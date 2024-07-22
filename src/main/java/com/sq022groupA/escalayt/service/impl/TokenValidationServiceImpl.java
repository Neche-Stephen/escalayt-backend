package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.model.Admin;
import com.sq022groupA.escalayt.entity.model.ConfirmationToken;
import com.sq022groupA.escalayt.repository.AdminRepository;
import com.sq022groupA.escalayt.repository.ConfirmationTokenRepository;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenValidationServiceImpl implements TokenValidationService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AdminRepository adminRepository;

    @Override
    public String validateToken(String token) {

        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepository.findByToken(token);
        if (confirmationTokenOptional.isEmpty()) {
            return "Invalid token";
        }

        ConfirmationToken confirmationToken = confirmationTokenOptional.get();

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "Token has expired";
        }

        Admin admin = confirmationToken.getAdmin();
        admin.setEnabled(true);
        adminRepository.save(admin);

        confirmationTokenRepository.delete(confirmationToken); //delete the token after successful verification

        return "Email confirmed successfully";
    }
}
