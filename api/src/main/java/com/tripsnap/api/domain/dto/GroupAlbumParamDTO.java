package com.tripsnap.api.domain.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupAlbumParamDTO {
    @Positive @Min(1)
    private Long groupId = 0L;
    @Positive @Min(1)
    private Long albumId = 0L;
}
