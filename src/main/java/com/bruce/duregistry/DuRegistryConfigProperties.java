package com.bruce.duregistry;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @date 2024/4/23
 */
@Data
@ConfigurationProperties(prefix = "duregistry")
public class DuRegistryConfigProperties {

    private List<String> serverList;
}
