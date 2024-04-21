package com.tripsnap.api.domain.dto;

import com.tripsnap.api.domain.Regexp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberPasswordEditDTO(
        @NotBlank
        @Size(min=1,max=100)
        String password,
        @NotBlank
        @Pattern(regexp = Regexp.PASSWORD)
        String newPassword
) {
}
