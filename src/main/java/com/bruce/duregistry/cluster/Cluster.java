package com.bruce.duregistry.cluster;

import com.bruce.duregistry.DuRegistryConfigProperties;
import com.bruce.duregistry.http.HttpInvoker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Register cluster
 * @date 2024/4/23
 */
@Slf4j
public class Cluster {

    @Value("${server.port}")
    String port;

    String host;

    Server MYSELF;
    DuRegistryConfigProperties registryConfigProperties;

    public Cluster(DuRegistryConfigProperties registryConfigProperties) {
        this.registryConfigProperties = registryConfigProperties;
    }

    @Getter
    private List<Server> servers;

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long timeout = 5_000;

    public void init() {
        try {
            host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress();
            log.info(" ===> findFirstNonLoopbackHostInfo = " + host);
        }catch (Exception e){
            host = "127.0.0.1";
        }

        MYSELF = new Server("http://" + host + ":" + port, true, false, -1L);
        log.info(" ===> myself = " + MYSELF);

        List<Server> servers = new ArrayList<>();
        for (String url : registryConfigProperties.getServerList()){
            Server server = new Server();
            if(url.contains("localhost")) {
                url = url.replace("localhost", host);
            } else if(url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", host);
            }
            if(url.equals(MYSELF.getUrl())) {
                servers.add(MYSELF);
            } else {
                server.setUrl(url);
                server.setStatus(false);
                server.setLeader(false);
                server.setVersion(-1);
                servers.add(server);
            }
        }
        this.servers = servers;

        executor.scheduleAtFixedRate(() -> {
            updateServer();
            electLeader(servers);
        }, 0, timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    private void electLeader(List<Server> servers){
        List<Server> masters = servers.stream().filter(Server::isStatus)
                .filter(Server::isLeader).toList();
        if(masters.isEmpty()){
            log.info(" ===>>> [ELECT] elect for no leader: " + servers);
            elect(servers);
        }else if(masters.size() > 1){
            log.warn(" ===>>> [ELECT] elect for more than one leader: " + servers);
            elect(servers);
        }else {
            log.debug(" ===>>> no need election for leader: " + masters.get(0));
        }
    }

    private void elect(List<Server> servers) {
        // 1.各种节点自己选，算法保证大家选的是同一个
        // 2.外部有一个分布式锁，谁拿到锁，谁是主
        // 3.分布式一致性算法，比如paxos,raft，，很复杂
        Server candidate = null;
        for (Server server : servers) {
            server.setLeader(false);
            if(server.isStatus()){
                if(candidate == null){
                    candidate = server;
                }else if(candidate.hashCode() > server.getVersion()){
                    candidate = server;
                }
            }
        }
        if(candidate != null){
            candidate.setLeader(true);
            log.info(" ===>>> elect for leader: " + candidate);
        }else {
            log.info(" ===>>> elect failed for no leaders: " + servers);
        }

    }

    private void updateServer() {
        servers.forEach(server -> {
            try {
                Server serverInfo = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                log.info(" ===>>> health check success for " + serverInfo);
                if(serverInfo != null){
                    server.setStatus(true);
                    server.setLeader(server.isLeader());
                    server.setVersion(server.getVersion());
                }
            }catch (Exception e){
                log.info(" ===>>> health check failed for " + server);
                server.setStatus(false);
                server.setLeader(false);
            }
        });
    }


    public Server self() {
        return MYSELF;
    }

    public Server leader() {
        return this.servers.stream().filter(Server::isStatus)
                .filter(Server::isLeader).findFirst().orElse(null);
    }
}
