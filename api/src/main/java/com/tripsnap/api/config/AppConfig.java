package com.tripsnap.api.config;

import com.tripsnap.api.auth.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.tripsnap.api.repository")
public class AppConfig {
    @Bean
    public FilterRegistrationBean<LoginFilter> tenantFilterRegistration(LoginFilter filter) {
        FilterRegistrationBean<LoginFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
