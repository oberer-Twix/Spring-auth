package com.example.userverwaltung2.presentation.web.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    @AssertTrue(message = "Passwords must match")
    public boolean isValid() {
        return true;
    }
}
