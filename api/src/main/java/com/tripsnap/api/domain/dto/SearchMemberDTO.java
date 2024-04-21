package com.tripsnap.api.domain.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchMemberDTO {
    private String email;
    private String nickname;
    private String photo;
    @Setter
    private Boolean isFriend;
}
