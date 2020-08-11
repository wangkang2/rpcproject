package com.rpc.netty;

import com.rpc.annotation.RpcService;
import com.rpc.zkService.ZkService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NettyServer implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Map<String, Object> serviceMap = new HashMap<>();

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    @Value("${rpc.server.address}")
    private String rpcServiceAddress;

    @Autowired
    private ZkService zkService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);
        for(Object serviceBean:beans.values()){
            Class<?> clazz = serviceBean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for(Class<?> inter:interfaces){
                String interfaceName = inter.getName();
                logger.info("加载服务类:{}", interfaceName);
                serviceMap.put(interfaceName, serviceBean);
            }
        }
        logger.info("已加载全部服务接口：{}",serviceMap);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    public void start(){
        new Thread(() -> {
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        //设置日志
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            protected void initChannel(SocketChannel sc) throws Exception {
                                ChannelPipeline pipeline = sc.pipeline();
                                pipeline.addLast(new IdleStateHandler(0, 0, 30));
                                pipeline.addLast(new JSONEncoder());
                                pipeline.addLast(new JSONDecoder());
                                pipeline.addLast(new NettyServerHandler(serviceMap));
                            }
                        });

                ChannelFuture cf = null;
                String[] array = rpcServiceAddress.split(":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                cf = b.bind(host, port).sync();
                logger.info("RPC服务启动，ip：{}, 监听端口：{}",host, port);
                zkService.register(rpcServiceAddress);
                cf.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }


        }).start();
    }


}
