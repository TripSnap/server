package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinDTO(
        @Email @Size(min=1,max=50) @NotBlank
        String email,
        @Pattern(regexp = "/^(?=.*[a-zA-Z])(?=.*[0-9]).{12,100}$/")
        String password,
        @Pattern(regexp = "/^[a-zA-Z가-힣][0-9a-zA-Z가-힣]{4,19}$/")
        String nickname
) {
}
