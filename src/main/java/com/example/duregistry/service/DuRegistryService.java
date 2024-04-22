package com.example.duregistry.service;

import com.example.duregistry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * Default implement of RegistryService
 * @date 2024/4/22
 */
@Slf4j
public class DuRegistryService implements RegistryService{

    MultiValueMap<String,InstanceMeta> REGISTRY = new LinkedMultiValueMap();

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
        return instance;
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if(metas == null || metas.isEmpty()){
            return null;
        }
        if(metas.contains(instance)){
            log.info("=====> unregister instance {}", instance.toUrl());
            REGISTRY.remove(service, instance);
            instance.setStatus(false);
            return instance;
        }
        return null;
    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return REGISTRY.get(service);
    }
}
