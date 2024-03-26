package com.tripsnap.api.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    ApplicationContext context;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CommonSecurityConfig commonSecurityConfig = context.getBean(CommonSecurityConfig.class);
        http.apply(commonSecurityConfig);
        return http.build();
    }
}
