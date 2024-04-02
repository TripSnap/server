package com.tripsnap.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchMemberDTO {
    private String email;
    private String nickname;
    private String photo;
    @Setter
    private Boolean isFriend;
}
