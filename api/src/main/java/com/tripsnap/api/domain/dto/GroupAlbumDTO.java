package com.tripsnap.api.domain.dto;

import java.util.List;

public record GroupAlbumDTO(
        Long albumId,
        Double latitude,
        Double longitude,
        String address,
        List<AlbumPhotoDTO> albumPhotoList,
        MemberDTO member
) {
}
