package com.example.duregistry.service;

import com.example.duregistry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default implement of RegistryService
 * @date 2024/4/22
 */
@Slf4j
public class DuRegistryService implements RegistryService{

    final static MultiValueMap<String,InstanceMeta> REGISTRY = new LinkedMultiValueMap();

    final static Map<String, Long> VERSIONS = new ConcurrentHashMap<>();

    public final static Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();

    public final static AtomicLong VERSION = new AtomicLong(0);

    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if(metas != null && !metas.isEmpty()){
            if(metas.contains(instance)){
                log.info("instance {} already registered", instance.toUrl());
                instance.setStatus(true);
                return instance;
            }
        }
        log.info("=====> register instance {}", instance.toUrl());
        REGISTRY.add(service, instance);
        instance.setStatus(true);
        renew(service, instance);
        return instance;
    }

    private static void renew(String service, InstanceMeta instance) {
        TIMESTAMPS.put(service + "@" + instance.toUrl(),System.currentTimeMillis());
        VERSIONS.put(service,VERSION.incrementAndGet());
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if(metas == null || metas.isEmpty()){
            return null;
        }
        metas.removeIf(m -> m.equals(instance));

        log.info("=====> unregister instance {}", instance.toUrl());
        REGISTRY.remove(service, instance);
        instance.setStatus(false);
        renew(service, instance);
        return instance;
    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return REGISTRY.get(service);
    }
}
