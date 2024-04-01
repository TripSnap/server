package com.tripsnap.api.service;


import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.entity.TemporaryMember;
import com.tripsnap.api.domain.mapstruct.MemberMapper;
import com.tripsnap.api.repository.MemberRepository;
import com.tripsnap.api.repository.TemporaryMemberRepository;
import com.tripsnap.api.utils.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class JoinService {
    final private TemporaryMemberRepository temporaryMemberRepository;
    final private MemberRepository memberRepository;
    final private MemberMapper memberMapper;
    final private int CODE_PERIOD = 60 * 60 * 24;

    @Transactional
    public boolean join(JoinDTO joinDTO) {
        if(checkEmail(joinDTO.email())) {
            TemporaryMember member = memberMapper.toTemporaryMember(joinDTO);
            member.setToken(RandomUtil.getRandomString(30));
            temporaryMemberRepository.save(member);
            return true;
        }
        return false;
    }

    public boolean checkEmail(String email) {
        Optional<Member> optMember = memberRepository.findByEmail(email);

        if(optMember.isEmpty()) {
            Optional<TemporaryMember> optTmpMember =temporaryMemberRepository.findByEmail(email);
            return optTmpMember.isEmpty();
        } else {
            return false;
        }
    }

    public boolean verifyEmailCode(String email, String code) {
        Optional<TemporaryMember> optMember = temporaryMemberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            TemporaryMember member = optMember.get();
            if(member.getToken().equals(code)) {
                LocalDateTime expiration = member.getCreatedAt().plusSeconds(CODE_PERIOD);
                return LocalDateTime.now().isBefore(expiration);
            }
        }
        return false;
    }
}
