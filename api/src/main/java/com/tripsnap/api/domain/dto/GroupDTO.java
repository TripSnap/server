package com.tripsnap.api.domain.dto;

public record GroupDTO(
    Long id,
    MemberDTO owner,
    String createdAt
) {}