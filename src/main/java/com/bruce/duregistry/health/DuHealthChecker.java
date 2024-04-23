package com.bruce.duregistry.health;

import com.bruce.duregistry.model.InstanceMeta;
import com.bruce.duregistry.service.DuRegistryService;
import com.bruce.duregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @date 2024/4/23
 */
@Slf4j
public class DuHealthChecker implements HealthChecker{

    RegistryService registryService;

    public DuHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    long timeout = 20_000;

    @Override
    public void start() {
        executor.scheduleWithFixedDelay(() -> {
            log.info(" ======> Health checker running...");
            long now = System.currentTimeMillis();
            DuRegistryService.TIMESTAMPS.keySet().stream().forEach(serviceAndInst -> {
                long timestamp = DuRegistryService.TIMESTAMPS.get(serviceAndInst);
                if (now - timestamp > timeout) {
                    log.info(" ======> Health checker : {} is down", serviceAndInst);
                    int index = serviceAndInst.indexOf("@");
                    String service = serviceAndInst.substring(0, index);
                    String url = serviceAndInst.substring(index + 1);
                    InstanceMeta instance = InstanceMeta.from(url);
                    registryService.unregister(service, instance);
                    DuRegistryService.TIMESTAMPS.remove(serviceAndInst);
                }
            });
        },10,10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        executor.shutdown();
    }
}
