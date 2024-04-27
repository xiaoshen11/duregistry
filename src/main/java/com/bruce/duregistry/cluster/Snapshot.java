package com.bruce.duregistry.cluster;

import com.bruce.duregistry.model.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

/**
 * @date 2024/4/27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Snapshot {
    LinkedMultiValueMap<String, InstanceMeta> REGISTRY;
    Map<String, Long> VERSIONS;
    Map<String, Long> TIMESTAMPS;
    long version;
}
