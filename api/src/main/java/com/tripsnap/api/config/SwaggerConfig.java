package com.tripsnap.api.config;


import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

@ConditionalOnProperty(
        name = {"springdoc.swagger-ui.enabled"},
        matchIfMissing = true
)

@Configuration
public class SwaggerConfig {

    @Autowired
    ApplicationContext context;

    @Bean
    @Primary
    public SwaggerUiConfigParameters swaggerUiConfig(SwaggerUiConfigParameters config) {
        config.setSupportedSubmitMethods(List.of());
        return config;
    }

    @Bean
    @Primary
    @DependsOn("commonSecurityConfig")
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        CommonSecurityConfig commonSecurityConfig = context.getBean(CommonSecurityConfig.class);
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http.apply(commonSecurityConfig);
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        mvcMatcherBuilder.pattern("/v3/api-docs"),
                        mvcMatcherBuilder.pattern("/v3/api-docs/**"),
                        mvcMatcherBuilder.pattern("/swagger*"),
                        mvcMatcherBuilder.pattern("/swagger-ui/**"),
                        mvcMatcherBuilder.pattern("/api-docs*")
                ).permitAll()
        );
        return http.build();
    }
}