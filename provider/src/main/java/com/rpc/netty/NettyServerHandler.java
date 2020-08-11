package com.rpc.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rpc.entity.Request;
import com.rpc.entity.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    public Map<String, Object> serviceMap;

    public NettyServerHandler(Map<String, Object> serviceMap){
        this.serviceMap = serviceMap;
    }

    public void channelActive(ChannelHandlerContext ctx){
        logger.info("客户端连接成功!"+ctx.channel().remoteAddress());
    }

    public void channelInactive(ChannelHandlerContext ctx){
        logger.info("客户端断开连接!{}",ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        Request request = JSONObject.parseObject(JSON.toJSONString(msg),Request.class);
        if("heartBeat".equals(request.getMethodName())){
            logger.info("客户端心跳信息..."+ctx.channel().remoteAddress());
        }else{
            logger.info("RPC请求接口：{}，请求方法：{}",request.getClassName(),request.getMethodName());
            Response response = new Response();
            response.setResponseId(request.getRequestId());
            try {
                Object result = this.handler(request);
                response.setCode(0);
                response.setMessage("success");
                response.setData(result);
            } catch (Exception e) {
                e.printStackTrace();
                response.setCode(1);
                response.setMessage(e.getMessage());
                logger.error("rpc service error : {}",e);
            }
            ctx.writeAndFlush(response);
        }
    }

    private Object handler(Request request) throws Exception {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        Object serviceBean = serviceMap.get(className);
        if(serviceBean!=null){
            Class<?> serviceClass = serviceBean.getClass();
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, getParameters(parameterTypes, parameters));
        }else{
            throw new Exception("未找到服务接口"+className+methodName);
        }
    }

    private Object[] getParameters(Class<?>[] parameterTypes, Object[] parameters){
        if(parameters==null || parameters.length==0){
            return parameters;
        }else{
            Object[] newparameters = new Object[parameters.length];
            for(int i=0;i<parameters.length;i++){
                newparameters[i] = JSON.parseObject(parameters[i].toString(),parameterTypes[i]);
            }
            return newparameters;
        }
    }

}
