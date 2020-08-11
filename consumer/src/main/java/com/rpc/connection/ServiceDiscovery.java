package com.rpc.connection;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceDiscovery {

    @Value("${registry.address}")
    private String registryAddress;

    //会话超时时间，5秒不使用自动释放连接
    private static final int SESSION_OUTTIME = 5000;

    //连接超时时间
    private static final int CONNECTION_OUTTIME = 5000;

    @Autowired
    ConnectManage connectManage;

    // 服务地址列表
    private volatile List<String> addressList = new ArrayList<>();

    Logger logger = LoggerFactory.getLogger(this.getClass());

    CuratorFramework client;

    @PostConstruct
    public void init() {
        client = connectServer();
        if (client != null) {
            updateConnectedServer();
        }
    }

    private CuratorFramework connectServer() {
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
        return client;
    }

    private void updateConnectedServer() {
        getNodeData();
        connectManage.updateConnectServer(addressList);
    }

    private void getNodeData() {
        try {
            byte[] bytes = client.getData().forPath("/rpc/provider");
            String address = new String(bytes);
            addressList.add(address);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
