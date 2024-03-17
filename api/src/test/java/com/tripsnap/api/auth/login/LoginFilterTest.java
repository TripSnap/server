package com.tripsnap.api.auth.login;

import com.tripsnap.api.auth.redis.RedisTestConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

// TODO: login test 추가


@SpringBootTest
@AutoConfigureMockMvc
@Import(RedisTestConfig.class)
class LoginFilterTest {
// 로그인 성공, 로그인 실패

}