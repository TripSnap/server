package com.tripsnap.api.domain.dto;

public record PresignedUrlResultDTO(
        String uploadUrl,
        String filename
) {
}
