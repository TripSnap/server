package com.tripsnap.api.auth;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.tripsnap.api.auth.redis.RedisTestConfig;
import com.tripsnap.api.auth.redis.RefreshToken;
import com.tripsnap.api.auth.redis.RefreshTokenRepository;
import com.tripsnap.api.auth.vo.DecryptedToken;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.testcontainers.containers.GenericContainer;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataRedisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RedisTestConfig.class, TokenService.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenServiceTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    TokenService tokenService;

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

    @Test
    @DisplayName("Access Token 생성")
    void createAccessToken() {
        String email = "test54@naver.com";
        String role = "USER";
        String accessToken = tokenService.createAccessToken(email, role);
        Assertions.assertDoesNotThrow(() -> {
            DecryptedToken decryptedToken = tokenService.verifyAccessToken(accessToken);
            Assertions.assertEquals(decryptedToken.email(), email);
            Assertions.assertEquals(decryptedToken.role(), role);
            Assertions.assertFalse(decryptedToken.expired());
        });
    }

    @Test
    @DisplayName("Access Token 검증")
    void verifyAccessToken() {
        String email = "test54@naver.com";
        String role = "USER";
        String accessToken = tokenService.createAccessToken(email, role);
        Assertions.assertDoesNotThrow(() -> {
            DecryptedToken decryptedToken = tokenService.verifyAccessToken(accessToken);
            Assertions.assertEquals(decryptedToken.email(), email);
            Assertions.assertEquals(decryptedToken.role(), role);
        });
        Assertions.assertThrows(BadCredentialsException.class, () -> {
            tokenService.verifyAccessToken(accessToken+"dsjdf");
        });
    }

    @Test
    @DisplayName("만료된 Access Token 생성")
    void expireAccessToken() {
        String accessToken = tokenService.expireAccessToken();
        Assertions.assertDoesNotThrow(() -> {
            DecryptedToken decryptedToken = tokenService.verifyAccessToken(accessToken);
            Assertions.assertTrue(decryptedToken.expired());
        });
    }

    @Test
    @DisplayName("Refresh Token 생성 및 저장")
    void createRefreshToken() {
        String email = "test534@gmail.com";
        String uuid = tokenService.createRefreshToken(email);

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(email);
        Assertions.assertTrue(refreshToken.isPresent());

        Assertions.assertEquals(email, refreshToken.get().getEmail());
        Assertions.assertEquals(uuid, refreshToken.get().getUuid());
    }

    @Test
    @DisplayName("Refresh Token 검증")
    void verifyRefreshToken() {
        String email = "test534@gmail.com";
        String uuid = tokenService.createRefreshToken(email);

        assertDoesNotThrow(() -> {
            Assertions.assertTrue(tokenService.verifyRefreshToken(uuid, email));
        });

        assertThrows(BadCredentialsException.class, () -> {
            tokenService.verifyRefreshToken(uuid+"y", email);
        });
    }

    @Test
    @DisplayName("Refresh Token 삭제")
    void removeRefreshToken() {
        String email = "test534@gmail.com";
        String uuid = tokenService.createRefreshToken(email);

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(email);
        Assertions.assertTrue(refreshToken.isPresent());

        tokenService.removeRefreshToken(email);

        refreshToken = refreshTokenRepository.findById(email);
        Assertions.assertTrue(refreshToken.isEmpty());
    }
}