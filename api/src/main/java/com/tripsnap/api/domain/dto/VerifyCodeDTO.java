package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyCodeDTO(
        @Email @NotBlank
        String email,
        @Size(min=10, max=50) @NotBlank
        String code
) {
}
