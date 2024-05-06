package com.tripsnap.api.auth.login;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.google.gson.Gson;
import com.tripsnap.api.auth.JWTFilter;
import com.tripsnap.api.auth.TokenService;
import com.tripsnap.api.auth.logout.JWTLogoutHandler;
import com.tripsnap.api.auth.redis.RedisTestConfig;
import com.tripsnap.api.auth.vo.DecryptedToken;
import com.tripsnap.api.config.AppConfig;
import com.tripsnap.api.config.CommonSecurityConfig;
import com.tripsnap.api.config.SecurityConfig;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("로그인 필터 테스트")
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"springdoc.swagger-ui.enabled=false"})
@ContextConfiguration(classes = {CommonSecurityConfig.class, SecurityConfig.class, AppConfig.class, RedisTestConfig.class})
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({  LoginUserDetailsService.class, TokenService.class})
class LoginFilterTest {
    private MockMvc mvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    JWTFilter jwtFilter;
    @Autowired
    LoginFilter loginFilter;
    @Autowired
    LoginUserDetailsService loginUserDetailsService;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    TokenService tokenService;
    @MockBean
    JWTLogoutHandler JWTLogoutHandler;

    @MockBean
    MemberRepository memberRepository;

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

    @DisplayName("로그인 성공")
    @Test
    void loginSuccess() throws Exception {
        String email = "existUser@naver.com";
        Member member = Member.builder().email(email).password("password").build();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));


        this.mvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(Map.of("email", email, "password","password")))
        )
                .andExpect(status().isOk())
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Cookie cookie = response.getCookie("access-token");
                    Assertions.assertNotNull(cookie);

                    Assertions.assertDoesNotThrow(() -> {
                        DecryptedToken decryptedToken = tokenService.verifyAccessToken(cookie.getValue());
                        Assertions.assertEquals(decryptedToken.email(), email);
                    });
                })
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Assertions.assertTrue(response.containsHeader("Refresh-Token"));
                })
        ;
    }

    @DisplayName("로그인 실패 - 없는 회원")
    @Test
    void loginFail_NoExistUser() throws Exception {
        String noExistUserEmail = "noExistUser@naver.com";
        given(memberRepository.findByEmail(noExistUserEmail)).willReturn(Optional.empty());


        this.mvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(Map.of("email", noExistUserEmail, "password","password")))
                )
                .andExpect(status().is(401))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Cookie cookie = response.getCookie("access-token");
                    Assertions.assertNull(cookie);
                })
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Assertions.assertFalse(response.containsHeader("Refresh-Token"));
                })
        ;
    }


    @DisplayName("로그인 실패 - 비밀번호 오류")
    @Test
    void loginFail_WrongPassword() throws Exception {
        String existUserEmail = "existUser@naver.com";
        Member member = Member.builder().email(existUserEmail).password("password").build();
        given(memberRepository.findByEmail(existUserEmail)).willReturn(Optional.of(member));


        this.mvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new Gson().toJson(Map.of("email", existUserEmail, "password","wrongPassword")))
                )
                .andExpect(status().is(401))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Cookie cookie = response.getCookie("access-token");
                    Assertions.assertNull(cookie);
                })
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Assertions.assertFalse(response.containsHeader("Refresh-Token"));
                })
        ;
    }

}