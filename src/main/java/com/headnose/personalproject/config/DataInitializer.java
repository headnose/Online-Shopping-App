package com.headnose.personalproject.config;

import com.headnose.personalproject.service.AdminSetupService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner init(AdminSetupService adminSetupService) {
        return args -> adminSetupService.createAdminIfNotExists();
    }
}
