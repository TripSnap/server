package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.dto.MemberDTO;
import com.tripsnap.api.domain.dto.SearchMemberDTO;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.entity.TemporaryMember;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


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

    @Mapping(source = "createdAt", target = "joinDate", dateFormat = "yyyy/MM/dd")
    public abstract MemberDTO toMemberDTO(Member member);

    @Named(value="waitingMemberDTO")
    @Mapping(target="isWaiting", expression = "java(true)")
    public abstract MemberDTO toWaitingMemberDTO(Member source);

    public abstract SearchMemberDTO toSearchMemberDTO(Member member);

    public abstract List<MemberDTO> toMemberDTOList(List<Member> source);

    @IterableMapping(qualifiedByName = "waitingMemberDTO")
    public abstract List<MemberDTO> toWatingMemberDTOList(List<Member> source);
}
