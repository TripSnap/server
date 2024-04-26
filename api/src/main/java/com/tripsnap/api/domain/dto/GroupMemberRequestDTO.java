package com.tripsnap.api.domain.dto;

public record GroupMemberRequestDTO(
        Long id,
        String title,
        MemberDTO owner,
        String expiredAt
) {}
