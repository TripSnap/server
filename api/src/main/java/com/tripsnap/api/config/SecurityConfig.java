package com.tripsnap.api.config;


import com.tripsnap.api.auth.JWTFilter;
import com.tripsnap.api.auth.exception.AuthenticationExceptionHandler;
import com.tripsnap.api.auth.login.LoginFilter;
import com.tripsnap.api.auth.login.LoginSuccessHandler;
import com.tripsnap.api.auth.login.LoginUserDetailsService;
import com.tripsnap.api.auth.logout.JWTLogoutHandler;
import com.tripsnap.api.auth.logout.JWTLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import static jakarta.servlet.DispatcherType.ERROR;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    ApplicationContext context;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(ERROR).permitAll()
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .headers(header -> header.frameOptions(options -> options.sameOrigin()))
                .addFilterBefore(jwtFilter(), AuthorizationFilter.class)
                .addFilterBefore(loginFilter( providerManager()), AuthorizationFilter.class)
                .addFilterBefore(logoutFilter(),  AuthorizationFilter.class)
                .logout((logout) ->logout.disable())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new AuthenticationExceptionHandler()));
        return http.build();
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

}
