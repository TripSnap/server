package com.tripsnap.api.auth;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.tripsnap.api.auth.login.LoginUserDetailsService;
import com.tripsnap.api.auth.logout.JWTLogoutHandler;
import com.tripsnap.api.auth.redis.RedisTestConfig;
import com.tripsnap.api.auth.redis.RefreshToken;
import com.tripsnap.api.auth.redis.RefreshTokenRepository;
import com.tripsnap.api.config.AppConfig;
import com.tripsnap.api.config.CommonSecurityConfig;
import com.tripsnap.api.config.SecurityConfig;
import com.tripsnap.api.controller.AuthController;
import com.tripsnap.api.utils.TimeUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.AeadAlgorithm;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("JWT 필터 테스트")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CommonSecurityConfig.class, SecurityConfig.class,  AppConfig.class, RedisTestConfig.class})
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({TokenService.class, JWTFilterTest.TestController.class, AuthController.class})
class JWTFilterTest {

    @RestController
    static class TestController {
        @RequestMapping("/test")
        public ResponseEntity test() {
            return ResponseEntity.ok("test controller");
        }
    }

    @Autowired
    WebApplicationContext context;

    @MockBean
    LoginUserDetailsService LoginUserDetailsService;
    @MockBean
    JWTLogoutHandler JWTLogoutHandler;
    @Autowired
    TokenService tokenService;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    private MockMvc mvc;
    private GenericContainer redis;

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

    @DisplayName("정상 토큰")
    @Test
    void normalToken() throws Exception {
        String email = "test4t@naver.com";
        String accessToken = tokenService.createAccessToken(email, "ROLE_USER");

        mvc
                .perform(
                        get("/test")
                                .header(HttpHeaders.AUTHORIZATION, tokenService.TOKEN_TYPE + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> Assertions.assertFalse(result.getResponse().containsHeader(HttpHeaders.WWW_AUTHENTICATE)));
    }

    @DisplayName("잘못된 토큰")
    @Test
    void wrongToken() throws Exception {
        String email = "test4t@naver.com";
        String accessToken = tokenService.createAccessToken(email, "ROLE_USER");

        mvc
                .perform(
                        get("/test")
                                .header(HttpHeaders.AUTHORIZATION, tokenService.TOKEN_TYPE + accessToken + "abcde")
                )
                .andExpect(status().is(401))
                .andExpect(result -> result.getResponse().containsHeader(HttpHeaders.WWW_AUTHENTICATE));
    }

    @DisplayName("access token 만료")
    @Test
    void expiredAccessToken() throws Exception {
        String email = "test4t@naver.com";
        String expiredAccessToken = expiredAccessToken(email);
        String refreshToken = tokenService.createRefreshToken(email);

        Assertions.assertDoesNotThrow(() -> tokenService.verifyRefreshToken(refreshToken, email));

        // 정상 요청, 토큰 만료로 인해 401
        mvc
                .perform(
                        get("/test")
                                .header(HttpHeaders.AUTHORIZATION, tokenService.TOKEN_TYPE + expiredAccessToken)
                )
                .andExpect(status().is(401))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Assertions.assertTrue(response.containsHeader(HttpHeaders.WWW_AUTHENTICATE));
                    Assertions.assertEquals(response.getHeader(HttpHeaders.WWW_AUTHENTICATE), "Refresh-Token");
                });


        // refresh 요청
        // accessToken 재발급
        mvc
                .perform(
                        post("/refresh").param("grant_type","refresh_token").param("token", refreshToken)
                                .header(HttpHeaders.AUTHORIZATION, tokenService.TOKEN_TYPE + expiredAccessToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Assertions.assertTrue(response.containsHeader(HttpHeaders.AUTHORIZATION));
                    String accessToken = response.getHeader(HttpHeaders.AUTHORIZATION).replaceFirst(tokenService.TOKEN_TYPE, "");
                    Assertions.assertDoesNotThrow(() -> tokenService.verifyAccessToken(accessToken));
                });
    }

    @DisplayName("refresh token 만료")
    @Test
    void expiredRefreshToken() throws Exception {
        String email = "test4t@naver.com";
        String expiredAccessToken = expiredAccessToken(email);
        String refreshToken = expiredRefreshToken(email);

        // 정상 요청, 토큰 만료로 인해 401
        mvc
                .perform(
                        get("/test")
                                .header(HttpHeaders.AUTHORIZATION, tokenService.TOKEN_TYPE + expiredAccessToken)
                )
                .andExpect(status().is(401))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Assertions.assertTrue(response.containsHeader(HttpHeaders.WWW_AUTHENTICATE));
                    Assertions.assertEquals(response.getHeader(HttpHeaders.WWW_AUTHENTICATE), "Refresh-Token");
                });


        // refresh 요청
        // refreshToken 만료로 인해 재발급 X
        mvc
                .perform(
                        post("/refresh").param("grant_type","refresh_token").param("token", refreshToken)
                                .header(HttpHeaders.AUTHORIZATION, tokenService.TOKEN_TYPE + expiredAccessToken)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Assertions.assertTrue(response.containsHeader(HttpHeaders.WWW_AUTHENTICATE));
                    Assertions.assertFalse(response.containsHeader(HttpHeaders.AUTHORIZATION));
                });
    }

    private String expiredAccessToken(String email) {
        Date date = TimeUtil.timeCalc(new Date(), -10);
        AeadAlgorithm enc = (AeadAlgorithm) ReflectionTestUtils.getField(tokenService, "enc");
        SecretKey key = (SecretKey) ReflectionTestUtils.getField(tokenService, "key");
        return Jwts.builder()
                .header().and()
                .issuer(email)
                .issuedAt(date)
                .expiration(date)
                .claims(Map.of("role", "ROLE_USER"))
                .encryptWith(key, enc)
                .compact();
    }

    private String expiredRefreshToken(String email) {
        LocalDateTime issuedAt = LocalDateTime.now();
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email).uuid(String.valueOf(UUID.randomUUID()))
                .expiration(issuedAt.minusSeconds(100))
                .build();

        RefreshToken token = refreshTokenRepository.save(refreshToken);
        return token.getUuid();
    }
}