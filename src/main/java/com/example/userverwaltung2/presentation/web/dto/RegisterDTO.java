package com.example.userverwaltung2.presentation.web.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class RegisterDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    @NotBlank
    private String repeatPassword;

    @AssertTrue(message = "Passwords must match")
    public boolean isValid() {
        // insert pw validation
        return password.equals(repeatPassword);
    }


}
