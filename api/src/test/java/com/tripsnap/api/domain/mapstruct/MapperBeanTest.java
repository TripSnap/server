package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.entity.TemporaryMember;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;


@DisplayName("MapStruct Mapper Bean Inject 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
        classes = MapperBeanTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class MapperBeanTest {
    @Autowired
    MemberMapper memberMapper;
    @Test
    @DisplayName("PasswordEncoder 주입 테스트")
    void passwordEncoderTest() {
        JoinDTO dto = new JoinDTO("test_email@naver.com", "paskdng@#!dk", "닉네임2");
        Assertions.assertDoesNotThrow(() -> {
            Object passwordEncoder = ReflectionTestUtils.getField(memberMapper, "passwordEncoder");
            Assertions.assertNotNull(passwordEncoder);
            TemporaryMember temporaryMember = memberMapper.toTemporaryMember(dto);
        });
    }
}