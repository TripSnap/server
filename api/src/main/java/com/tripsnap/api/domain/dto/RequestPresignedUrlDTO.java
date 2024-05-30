package com.tripsnap.api.domain.dto;

import com.tripsnap.api.domain.Regexp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RequestPresignedUrlDTO(
        @Pattern(regexp = Regexp.TYPE_IMAGE) @NotBlank
        String type
) {
}
