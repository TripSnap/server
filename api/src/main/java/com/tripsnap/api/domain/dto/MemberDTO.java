package com.tripsnap.api.domain.dto;

public record MemberDTO(
        String email,
        String nickname,
        String photo,
        String joinDate
) {
}
