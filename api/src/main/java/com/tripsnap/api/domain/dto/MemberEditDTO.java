package com.tripsnap.api.domain.dto;

import com.tripsnap.api.domain.Regexp;
import jakarta.validation.constraints.Pattern;

public record MemberEditDTO(
        @Pattern(regexp = Regexp.NICKNAME)
        String nickname,
        String photo
){}
