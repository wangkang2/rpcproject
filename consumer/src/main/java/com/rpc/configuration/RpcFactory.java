package com.rpc.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rpc.entity.Request;
import com.rpc.entity.Response;
import com.rpc.entity.User;
import com.rpc.netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by MACHENIKE on 2018-12-03.
 */
@Component
public class RpcFactory<T> implements InvocationHandler {

    @Autowired
    NettyClient client;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Request request = new Request();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        request.setRequestId(UUID.randomUUID().toString());
        //client.connection();
        Response response = JSON.parseObject(client.send(request).toString(), Response.class);
        Class<?> returnType = method.getReturnType();
        if(response.getCode()==0){
            Object data = response.getData();
            return JSONObject.parseObject(data.toString(), returnType);
        }
        return null;
    }
}
