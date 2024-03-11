package com.tripsnap.api.auth.redis;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;


@DataRedisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RedisTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeAll
    void init() {
        Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(16379), new ExposedPort(6379)));

        GenericContainer redis = new GenericContainer("redis")
                .withCreateContainerCmdModifier(cmd);
        redis.start();
    }

    @Test
    @DisplayName("redis 저장")
    void save() {
        String uuid = String.valueOf(UUID.randomUUID());
        String email = "test1234@naver.com";
        LocalDateTime localDateTime = LocalDateTime.now();

        RefreshToken token = RefreshToken.builder()
                .uuid(uuid)
                .email(email)
                .expiration(localDateTime.plusSeconds(10))
                .build();

        refreshTokenRepository.save(token);

        Optional<RefreshToken> savedToken = refreshTokenRepository.findById(email);
        Assertions.assertTrue(savedToken.isPresent());;
        Assertions.assertEquals(savedToken.get().getEmail(), email);
    }

    @Test
    @DisplayName("remove")
    void remove() {

        String uuid = String.valueOf(UUID.randomUUID());
        String email = "test1234@naver.com";
        LocalDateTime localDateTime = LocalDateTime.now();

        RefreshToken token = RefreshToken.builder()
                .uuid(uuid)
                .email(email)
                .expiration(localDateTime.plusSeconds(10))
                .build();

        refreshTokenRepository.save(token);
        refreshTokenRepository.deleteById(email);

        Assertions.assertEquals(refreshTokenRepository.count(), 0);

        Optional<RefreshToken> savedToken = refreshTokenRepository.findById(email);
        Assertions.assertTrue(savedToken.isEmpty());
    }

}