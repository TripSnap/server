package com.tripsnap.api.config;


import com.tripsnap.api.auth.LoginFilter;
import com.tripsnap.api.auth.LoginUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    ApplicationContext context;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable())
                .headers(header -> header.frameOptions(options -> options.sameOrigin()))
                .addFilterBefore(loginFilter( providerManager()), AuthorizationFilter.class);
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
        provider.setUserDetailsService(userDetailsService());
        return new ProviderManager(provider);
    }

    @Bean
    LoginFilter loginFilter(AuthenticationManager providerManager) {
        LoginFilter loginFilter = new LoginFilter("/login");
        loginFilter.setAuthenticationManager(providerManager);

        return loginFilter;
    }

    @Bean
    UserDetailsService userDetailsService() {
        LoginUserDetailsService bean = context.getBean(LoginUserDetailsService.class);
        return bean;
    }

}
