package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class PhotoParamDTO extends GroupAlbumParamDTO {
    @Positive
    private Long photoId = 0L;
}
