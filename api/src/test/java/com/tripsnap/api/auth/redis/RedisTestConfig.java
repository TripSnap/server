package com.tripsnap.api.auth.redis;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


@Configuration
public class RedisTestConfig {
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .build();

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 16379), clientConfig);
    }
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<byte[], byte[]> template = new RedisTemplate<byte[], byte[]>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}