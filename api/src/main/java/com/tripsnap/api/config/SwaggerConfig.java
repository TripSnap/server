package com.tripsnap.api.config;


import com.tripsnap.api.openapi.CustomOpenAPI;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import reactor.util.function.Tuples;

import java.util.List;

@ConditionalOnProperty(
        name = {"springdoc.swagger-ui.enabled"},
        havingValue = "true"
)
@Configuration
public class SwaggerConfig {

    @Autowired
    ApplicationContext context;

    @Bean
    @Primary
    @Lazy
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

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        CustomOpenAPI.Decorator
                .post().pathname("/login").tag("auth").summary("로그인")
                .requestBody(List.of(Tuples.of("email", "string"), Tuples.of("password", "string")))
                .response("200", "successful operation")
                .set(openAPI);

        CustomOpenAPI.Decorator
                .get().pathname("/logout").tag("auth").summary("로그아웃")
//                .requestBody(List.of(Tuples.of("email", "string"), Tuples.of("password", "string")))
                .response("200", "successful operation")
                .set(openAPI);

        return openAPI;
    }
}
