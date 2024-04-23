package com.example.duregistry;

import com.example.duregistry.health.DuHealthChecker;
import com.example.duregistry.health.HealthChecker;
import com.example.duregistry.service.DuRegistryService;
import com.example.duregistry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * configuration for all Beans.
 * @date 2024/4/22
 */
@Configuration
public class DuRegistryConfig {

    @Bean
    public RegistryService registryService(){
        return new DuRegistryService();
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker healthChecker(@Autowired RegistryService registryService) {
        return new DuHealthChecker(registryService);
    }
}
