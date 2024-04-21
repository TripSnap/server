package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public record PageDTO(
        @Positive
        int page,
        @Positive @Max(100)
        int pagePerCnt
) {
}
