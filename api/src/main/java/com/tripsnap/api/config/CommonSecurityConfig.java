package com.tripsnap.api.config;

import com.tripsnap.api.auth.JWTFilter;
import com.tripsnap.api.auth.Roles;
import com.tripsnap.api.auth.exception.AuthenticationExceptionHandler;
import com.tripsnap.api.auth.login.LoginFilter;
import com.tripsnap.api.auth.login.LoginSuccessHandler;
import com.tripsnap.api.auth.login.LoginUserDetailsService;
import com.tripsnap.api.auth.logout.JWTLogoutHandler;
import com.tripsnap.api.auth.logout.JWTLogoutSuccessHandler;
import com.tripsnap.api.filter.ServiceExceptionHandlingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

import static jakarta.servlet.DispatcherType.ERROR;

@Configuration
@EnableWebSecurity
public class CommonSecurityConfig implements SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    ApplicationContext context;

    @Value("${client.url}")
    private String[] clientUrls;

    @Override
    public void init(HttpSecurity http) throws Exception {
        HandlerMappingIntrospector introspector = context.getBean(HandlerMappingIntrospector.class);

        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        http
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(ERROR).permitAll()
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/login")).hasRole(Roles.ANONYMOUS)
                        .anyRequest().hasRole(Roles.USER)
                )
                .csrf(csrf -> csrf.disable())
                .headers(header -> header.frameOptions(options -> options.sameOrigin()))
                .addFilterBefore(jwtFilter(), AuthorizationFilter.class)
                .addFilterAfter(loginFilter(providerManager()), AuthorizationFilter.class)
                .addFilterAfter(logoutFilter(), AuthorizationFilter.class)
                .addFilterBefore(serviceExceptionHandlingFilter(), JWTFilter.class)
                .logout((logout) ->logout.disable())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationExceptionHandler()));
    }

    @Bean
    public AuthenticationExceptionHandler authenticationExceptionHandler() {
        return new AuthenticationExceptionHandler();
    }

    @Bean
    private static ServiceExceptionHandlingFilter serviceExceptionHandlingFilter() {
        return new ServiceExceptionHandlingFilter();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
//        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    AuthenticationManager providerManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        LoginUserDetailsService userDetailsService = context.getBean(LoginUserDetailsService.class);
        provider.setForcePrincipalAsString(true);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    LoginFilter loginFilter(AuthenticationManager providerManager) {
        LoginFilter loginFilter = new LoginFilter("/login");
        loginFilter.setAuthenticationManager(providerManager);
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        return loginFilter;
    }

    @Bean
    LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    @Bean
    LogoutFilter logoutFilter() {
        JWTLogoutHandler logoutHandler = context.getBean(JWTLogoutHandler.class);
        LogoutFilter logoutFilter = new LogoutFilter(new JWTLogoutSuccessHandler(), logoutHandler);
        return logoutFilter;
    }

    @Bean
    JWTFilter jwtFilter() {
        return new JWTFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(clientUrls));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of(HttpHeaders.WWW_AUTHENTICATE, "Refresh-Token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
