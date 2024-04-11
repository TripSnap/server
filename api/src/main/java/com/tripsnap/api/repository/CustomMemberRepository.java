package com.tripsnap.api.repository;

import com.tripsnap.api.domain.dto.MemberEditDTO;

public interface CustomMemberRepository {
    void updateMember(Long memberId, MemberEditDTO dto);
    boolean updateMemberPassword(Long memberId, String encodedNewPassword);
}
