package com.tripsnap.api.auth.logout;


import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.tripsnap.api.auth.TokenService;
import com.tripsnap.api.auth.login.LoginUserDetailsService;
import com.tripsnap.api.auth.redis.RedisTestConfig;
import com.tripsnap.api.auth.redis.RefreshTokenRepository;
import com.tripsnap.api.auth.vo.DecryptedToken;
import com.tripsnap.api.config.AppConfig;
import com.tripsnap.api.config.CommonSecurityConfig;
import com.tripsnap.api.config.SecurityConfig;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;

import java.util.function.Consumer;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("로그아웃 필터 테스트")
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"springdoc.swagger-ui.enabled=false"})
@ContextConfiguration(classes = {CommonSecurityConfig.class, SecurityConfig.class, AppConfig.class, RedisTestConfig.class})
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({TokenService.class, JWTLogoutHandler.class})
public class LogoutFilterTest {
    private MockMvc mvc;

    @Autowired
    WebApplicationContext context;

    @MockBean
    LoginUserDetailsService LoginUserDetailsService;
    @Autowired
    JWTLogoutHandler JWTLogoutHandler;
    @Autowired
    TokenService tokenService;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    GenericContainer redis;

    @BeforeAll
    void init() {
        Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(16379), new ExposedPort(6379)));

        redis = new GenericContainer("redis")
                .withCreateContainerCmdModifier(cmd);
        redis.start();
    }

    @AfterAll
    void end() {
        redis.stop();
    }

    @BeforeEach
    void setup() {
        this.mvc  = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }


    @Test
    @DisplayName("비로그인 회원이 요청")
    void anonymousRequest() throws Exception {
        mvc.perform(get("/logout"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("로그인 회원이 요청")
    void request() throws Exception {
        String email = "test4t@naver.com";
        String accessToken = tokenService.createAccessToken(email, "ROLE_USER");
        MockHttpServletResponse response = new MockHttpServletResponse();
        tokenService.setAccessTokenToResponse(accessToken, response);


        // 로그아웃 시 만료된 토큰을 반환한다.
        mvc.perform(get("/logout")
                        .cookie(response.getCookie("access-token")))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Cookie cookie = result.getResponse().getCookie("access-token");
                    Assertions.assertNotNull(cookie);

                    DecryptedToken decryptedToken = tokenService.verifyAccessToken(cookie.getValue());
                    Assertions.assertTrue(decryptedToken.expired());
                    Assertions.assertEquals(0, cookie.getMaxAge());
                });

    }

}
