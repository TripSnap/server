package com.tripsnap.api.domain.dto;

import com.tripsnap.api.domain.Regexp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberPasswordEditDTO(
        @NotBlank
        @Pattern(regexp = Regexp.PASSWORD)
        String password,
        @NotBlank
        @Pattern(regexp = Regexp.PASSWORD)
        String newPassword
) {
}
