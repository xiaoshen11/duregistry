package com.example.duregistry.service;

import com.example.duregistry.model.InstanceMeta;

import java.util.List;

/**
 * interface for registry service.
 * @date 2024/4/22
 */
public interface RegistryService {

    // 最基础的3个方法

    InstanceMeta register(String service, InstanceMeta instance);

    InstanceMeta unregister(String service, InstanceMeta instance);

    List<InstanceMeta> getAllInstances(String service);

    // todo 添加一些高级功能

}
