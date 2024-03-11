package com.tripsnap.api.service;

import com.tripsnap.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    final private MemberRepository memberRepository;

//    회원가입, 회원탈퇴, 회원 수정, 이메일 인증

}
