package com.tripsnap.api.domain.dto;

import com.tripsnap.api.domain.Regexp;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinDTO(
        @Email @Size(min=1,max=50) @NotBlank
        String email,
        @Pattern(regexp = Regexp.PASSWORD)
        @NotBlank
        String password,
        @Pattern(regexp = Regexp.NICKNAME)
        @NotBlank
        String nickname
) {
}
