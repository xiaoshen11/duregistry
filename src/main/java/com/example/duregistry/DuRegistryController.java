package com.example.duregistry;

import com.alibaba.fastjson.JSON;
import com.example.duregistry.model.InstanceMeta;
import com.example.duregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * rest controller for registry servicve
 * @date 2024/4/22
 */
@RestController
@Slf4j
public class DuRegistryController {

    @Autowired
    private RegistryService registryService;


    @RequestMapping("/reg")
    public InstanceMeta register(@RequestParam String service, @RequestBody InstanceMeta instance){
        log.info(" =====> register {} @ {}",service, instance);
        return  registryService.register(service,instance);
    }

    @RequestMapping("/unreg")
    public InstanceMeta unregister(@RequestParam String service, @RequestBody InstanceMeta instance){
        log.info(" =====> unregister {} @ {}",service, instance);
        return registryService.unregister(service,instance);
    }

    @RequestMapping("findAll")
    public List<InstanceMeta> findAllInstances(@RequestParam String service){
        log.info(" =====> findAllInstances {}",service);
        return registryService.getAllInstances(service);
    }

    public static void main(String[] args) {
        InstanceMeta meta = InstanceMeta.http("127.0.0.1",8081)
                .addParams(Map.of("env","dev","tag","RED"));
        System.out.println(JSON.toJSONString(meta));
    }

}
