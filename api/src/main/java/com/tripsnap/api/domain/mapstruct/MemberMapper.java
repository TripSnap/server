package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.entity.TemporaryMember;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class MemberMapper {
    @Autowired
    public void setPasswordEncode(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    private PasswordEncoder passwordEncoder;

    public TemporaryMember toTemporaryMember(JoinDTO source) {
        return TemporaryMember.builder()
                .email(source.email())
                .password(passwordEncoder.encode(source.password()))
                .nickname(source.nickname())
                .build();
    }
}
