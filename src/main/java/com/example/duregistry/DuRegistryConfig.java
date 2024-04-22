package com.example.duregistry;

import com.example.duregistry.service.DuRegistryService;
import com.example.duregistry.service.RegistryService;
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


}
