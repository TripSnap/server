package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record AlbumPhotoInsDTO(
        @NotBlank
        String photo
) {
}
