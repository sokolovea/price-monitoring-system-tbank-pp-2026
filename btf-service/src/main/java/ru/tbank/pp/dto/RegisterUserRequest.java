package ru.tbank.pp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 5, max = 30)
    private String password;
}
