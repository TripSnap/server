package com.tripsnap.api.domain.dto;

public record GroupDTO(
    Long id,
    String title,
    MemberDTO owner,
    String createdAt,
    boolean isOwner
) {}