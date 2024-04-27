package com.bruce.duregistry.cluster;

import com.bruce.duregistry.http.HttpInvoker;
import com.bruce.duregistry.service.DuRegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * check health for servers.
 * @date 2024/4/27
 */
@Slf4j
public class ServerHealth {

    final Cluster cluster;

    public ServerHealth(Cluster cluster) {
        this.cluster = cluster;
    }

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long interval = 5_000;

    public void checkServerHealth(){
        executor.scheduleAtFixedRate(() -> {
            updateServer();             // 1.更新服务状态
            doElect();       // 2.选主
            syncSnapshotFromLeader();   // 3.同步快照
        }, 0, interval, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    private void doElect() {
        new Election().electLeader(cluster.getServers());
    }

    private void syncSnapshotFromLeader() {
        Server self = cluster.self();
        Server leader = cluster.leader();
        log.debug(" ===>>> leader version: " + leader.getVersion()
                + ", my version: " + self.getVersion());
        if(!self.isLeader() && leader.getVersion() > self.getVersion()){
            log.debug(" ===>>> sync snapshot from leader: " + leader);
            Snapshot snapshot = HttpInvoker.httpGet(leader.getUrl() + "/snapshot", Snapshot.class);
            log.debug(" ===>>> sync and restore snapshot: " + snapshot);
            DuRegistryService.restore(snapshot);
        }
    }

    private void updateServer() {
        List<Server> servers = cluster.getServers();
        servers.stream().parallel().forEach(server -> {
            try {
                if(server.equals(cluster.self())){
                    return;
                }
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

}
