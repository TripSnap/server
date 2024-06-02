package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.*;


public record GroupAlbumInsDTO (
        @Size(min=10,max=100)
        String title,
        @Positive
        Long groupId,
        @DecimalMax("90.0") @DecimalMin("-90.0")
        Double latitude,
        @DecimalMax("180.0") @DecimalMin("-180.0")
        Double longitude,
        @NotBlank @Size(max=100)
        String address
) {
}
