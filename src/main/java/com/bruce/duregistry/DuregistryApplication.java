package com.bruce.duregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DuRegistryConfigProperties.class)
public class DuregistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DuregistryApplication.class, args);
    }

}
