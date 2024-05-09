package com.tripsnap.api.config;


import com.tripsnap.api.openapi.CustomOpenAPI;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import reactor.util.function.Tuples;

import java.util.List;

@ConditionalOnProperty(
        name = {"springdoc.swagger-ui.enabled"},
        havingValue = "true"
)
@Configuration
@SecurityScheme(
        name = "access-token",
        description = "access token cookie",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.COOKIE
)
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
        http
                .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        mvcMatcherBuilder.pattern("/v3/api-docs"),
                        mvcMatcherBuilder.pattern("/v3/api-docs/**"),
                        mvcMatcherBuilder.pattern("/swagger*"),
                        mvcMatcherBuilder.pattern("/swagger-ui/**"),
                        mvcMatcherBuilder.pattern("/api-docs*")
                ).permitAll()
        ).securityMatcher(new RegexRequestMatcher("^(?!/v3|/swagger-ui).*", null));
        return http.build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        CustomOpenAPI.Decorator
                .post().pathname("/login").tag("auth").summary("로그인")
                .requestBody(List.of(Tuples.of("email", "string"), Tuples.of("password", "string")))
                .response("200", "successful operation",
                        CustomOpenAPI.Decorator.header(List.of(
                                Tuples.of(HttpHeaders.SET_COOKIE, "access-token={token}"),
                                Tuples.of("Refresh-Token", "refresh token")
                        ))
                )
                .response("403", "권한에 맞지 않는 접근")
                .set(openAPI);

        CustomOpenAPI.Decorator
                .get().pathname("/logout").tag("auth").summary("로그아웃")
                .securityToken("access-token")
                .response("200", "successful operation")
                .response("401", "access token 만료")
                .response("401 ", "로그인 필요")
                .response("403", "권한에 맞지 않는 접근")
                .set(openAPI);

        return openAPI;
    }
}
