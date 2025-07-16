package com.bannakon.zentasks.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
