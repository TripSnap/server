package com.tripsnap.api.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.tripsnap.api.repository")
@Configuration
public class DataJpaConfig {
}
