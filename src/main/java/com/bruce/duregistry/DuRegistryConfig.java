package com.bruce.duregistry;

import com.bruce.duregistry.cluster.Cluster;
import com.bruce.duregistry.health.DuHealthChecker;
import com.bruce.duregistry.health.HealthChecker;
import com.bruce.duregistry.service.DuRegistryService;
import com.bruce.duregistry.service.RegistryService;
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

    @Bean(initMethod = "init")
    public Cluster cluster(@Autowired DuRegistryConfigProperties registryConfigProperties) {
        return new Cluster(registryConfigProperties);
    }

}
