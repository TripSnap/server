package com.tripsnap.api.domain.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.util.List;


public record GroupAlbumInsDTO (
        @Positive
        Long groupId,
        @Min(-90) @Max(90)
        Double latitude,
        @Min(-180) @Max(180)
        Double longitude,
        @NotBlank @Size(max=100)
        String address,
        @Nullable @Size(max=50)
        List<AlbumPhotoInsDTO> albumPhotoList
) {
}
