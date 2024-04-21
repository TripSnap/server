package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GroupInsDTO(
        @Size(min=10, max=100) @NotBlank
        String title,
        @Size(min=0, max=19)
        List<String> memberEmails
) {
}
