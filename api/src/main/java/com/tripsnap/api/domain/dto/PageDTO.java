package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public record PageDTO(
        @Min(0)
        int page,
        @Positive @Max(100)
        int pagePerCnt
) {
}
