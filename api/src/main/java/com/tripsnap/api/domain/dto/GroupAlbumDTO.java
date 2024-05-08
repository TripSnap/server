package com.tripsnap.api.domain.dto;

public record GroupAlbumDTO(
        Long id,
        String title,
        Double latitude,
        Double longitude,
        String address,
        MemberDTO member,
        String date
) {
}
