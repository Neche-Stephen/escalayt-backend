package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.payload.request.LoginRequestDto;
import com.sq022groupA.escalayt.payload.request.PasswordResetDto;
import com.sq022groupA.escalayt.payload.request.UserDetailsDto;
import com.sq022groupA.escalayt.payload.request.UserRequest;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import jakarta.mail.MessagingException;

public interface UserService {

    String registerUser(UserRequest registrationRequest) throws MessagingException;
    LoginResponse loginUser(LoginRequestDto loginRequestDto);
    void resetPassword(PasswordResetDto passwordResetDto);
    String editUserDetails(String username, UserRequest userRequest);

}
