package com.bruce.duregistry.service;

import com.bruce.duregistry.model.InstanceMeta;

import java.util.List;
import java.util.Map;

/**
 * interface for registry service.
 * @date 2024/4/22
 */
public interface RegistryService {

    // 最基础的3个方法

    InstanceMeta register(String service, InstanceMeta instance);

    InstanceMeta unregister(String service, InstanceMeta instance);

    List<InstanceMeta> getAllInstances(String service);

    // 添加一些高级功能
    long renew(InstanceMeta instance,String... service);

    Long version(String service);

    Map<String, Long> versions(String... services);

}
