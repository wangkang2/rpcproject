package com.rpc.zkService;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZkService {

    private static final Logger logger = LoggerFactory.getLogger(ZkService.class);

    //会话超时时间，5秒不使用自动释放连接
    private static final int SESSION_OUTTIME = 5000;

    //连接超时时间
    private static final int CONNECTION_OUTTIME = 5000;

    @Value("${registry.address}")
    private String registryAddress;
    //zk根节点
    private static final String PARENT_PATH = "/rpc";

    public void register(String data){

        //重试策略 间隔1秒重试，重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        //curator工厂构建
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(registryAddress)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .connectionTimeoutMs(CONNECTION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        //启动客户端
        client.start();

        try {
            Stat stat = client.checkExists().forPath(PARENT_PATH +"/provider");
            if(stat==null){
                client.create()
                        .creatingParentContainersIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(PARENT_PATH +"/provider", data.getBytes());
                logger.info("创建zookeeper数据节点 ({} => {})", PARENT_PATH +"/provider", data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
