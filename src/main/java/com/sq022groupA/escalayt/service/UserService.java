package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.payload.response.UserRegistrationResponse;
import jakarta.mail.MessagingException;

public interface UserService {
    LoginResponse loginUser(LoginRequestDto loginRequestDto);
}