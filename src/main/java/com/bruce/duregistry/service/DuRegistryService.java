package com.bruce.duregistry.service;

import com.bruce.duregistry.cluster.Snapshot;
import com.bruce.duregistry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Default implement of RegistryService
 * @date 2024/4/22
 */
@Slf4j
public class DuRegistryService implements RegistryService{

    final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap();

    final static Map<String, Long> VERSIONS = new ConcurrentHashMap<>();

    public final static Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();

    public final static AtomicLong VERSION = new AtomicLong(0);

    @Override
    public synchronized InstanceMeta register(String service, InstanceMeta instance) {
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
        renew(instance,service);
        VERSIONS.put(service,VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public synchronized InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if(metas == null || metas.isEmpty()){
            return null;
        }
        metas.removeIf(m -> m.equals(instance));

        log.info("=====> unregister instance {}", instance.toUrl());
        REGISTRY.remove(service, instance);
        instance.setStatus(false);
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return REGISTRY.get(service);
    }

    public synchronized long renew(InstanceMeta instance,String... services) {
        long now = System.currentTimeMillis();
        for (String service : services) {
            TIMESTAMPS.put(service+"@"+instance.toUrl(), now);
        }
        return now;
    }

    public Long version(String service) {
        return VERSIONS.get(service);
    }

    public Map<String, Long> versions(String... services) {
        return Arrays.stream(services)
                .collect(Collectors.toMap(x->x, VERSIONS::get, (a, b)->b));
    }

    public static synchronized Snapshot snapshot() {
        LinkedMultiValueMap<String, InstanceMeta> registry = new LinkedMultiValueMap<>();
        registry.addAll(REGISTRY);
        Map<String, Long> versions = new HashMap<>(VERSIONS);
        Map<String, Long> timestamps = new HashMap<>(TIMESTAMPS);
        return new Snapshot(registry, versions, timestamps, VERSION.get());
    }

    public static synchronized long restore(Snapshot snapshot) {
        REGISTRY.clear();
        REGISTRY.addAll(snapshot.getREGISTRY());
        VERSIONS.clear();
        VERSIONS.putAll(snapshot.getVERSIONS());
        TIMESTAMPS.clear();
        TIMESTAMPS.putAll(snapshot.getTIMESTAMPS());
        VERSION.set(snapshot.getVersion());
        return snapshot.getVersion();
    }
}
