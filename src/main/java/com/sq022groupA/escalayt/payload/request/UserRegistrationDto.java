package com.sq022groupA.escalayt.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.security.SecureRandom;
import java.util.Random;

@Data
@Builder
public class UserRegistrationDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Username did not generate")
    private String username;

    @NotBlank(message = "Password did not generate")
    private String password;

    private long createdUnder;

    public UserRegistrationDto(String fullName, String email, String phoneNumber, String jobTitle, String department) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.jobTitle = jobTitle;
        this.department = department;
        this.username = generateUserName(fullName);
        this.password = generatePassword();
    }

    private String generateUserName(String fullName) {
        String firstFourLetters = fullName.replaceAll("\\s+", "").substring(0, Math.min(fullName.length(), 4)).toLowerCase();
        int randomNumbers = new Random().nextInt(900) + 100; // 3-digit random number
        return firstFourLetters + randomNumbers;
    }

    private String generatePassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
