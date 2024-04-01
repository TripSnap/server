package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

public record PageDTO(
        @Positive
        int page,
        @Positive @Max(100)
        int pagePerCnt
) {
}
