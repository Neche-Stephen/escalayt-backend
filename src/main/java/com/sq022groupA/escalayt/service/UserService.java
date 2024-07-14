package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.payload.request.LoginRequestDto;
import com.sq022groupA.escalayt.payload.request.UserDetailsDto;
import com.sq022groupA.escalayt.payload.request.UserRequest;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import jakarta.mail.MessagingException;

public interface UserService {

    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);

    void editUserDetails(Long id, UserDetailsDto userDetailsDto);
}
