package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.entity.TemporaryMember;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;


@DisplayName("MapStruct Mapper 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapperTest {

    PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
    MemberMapper memberMapper;

    @BeforeAll
    void init() {
        memberMapper = new MemberMapperImpl();
        ReflectionTestUtils.setField(memberMapper, "passwordEncoder", passwordEncoder);
    }

    @Test
    @DisplayName("dto -> entity 변환(기본)")
    void dtoToEntity() {
        JoinDTO dto = new JoinDTO("test_email@naver.com", "paskdng@#!dk", "닉네임2");
        TemporaryMember temporaryMember = memberMapper.toTemporaryMember(dto);

        Assertions.assertEquals(dto.email(), temporaryMember.getEmail());
        Assertions.assertEquals(dto.password(), temporaryMember.getPassword());
        Assertions.assertEquals(dto.nickname(), temporaryMember.getNickname());
        Assertions.assertNull(temporaryMember.getId());
        Assertions.assertNull(temporaryMember.getToken());
    }
}