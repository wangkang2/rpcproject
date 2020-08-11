package com.rpc.configuration;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

public class RpcFactoryBean<T> implements FactoryBean<T> {

    private Class<T> rpcInterface;

    @Autowired
    RpcFactory<T> factory;

    public RpcFactoryBean(){
        System.out.println("构造函数1");
    }

    public RpcFactoryBean(Class<T> rpcInterface){
        this.rpcInterface = rpcInterface;
        System.out.println(rpcInterface);
    }

    public T getObject() throws Exception {
        System.out.println("get object");
        return getRpc();
    }

    public Class<?> getObjectType() {
        System.out.println(this.rpcInterface);
        return this.rpcInterface;
    }

    public boolean isSingleton() {
        System.out.println("singleton");
        return true;
    }

    public <T> T getRpc() {
        return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class[] { rpcInterface },factory);
    }
}
