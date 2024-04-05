package com.tripsnap.api.domain.dto;

public record MemberDTO(
        String email,
        String nickname,
        String photo,
        String joinDate,
        boolean isWaiting
) {
    public MemberDTO(
            String email,
            String nickname,
            String photo,
            String joinDate
    ) {
        this(email,nickname,photo,joinDate,false);
    }
}
