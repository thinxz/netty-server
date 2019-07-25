package com.thinxz.server.config;

import com.thinxz.server.handler.MsgProcessorHandler;
import com.thinxz.server.server.ConnectionManager;
import com.thinxz.server.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NettyServerProperty.class)
public class NettyServerConfig {

    @Autowired
    private NettyServerProperty nettyServerProperty;

    @Bean
    MsgProcessorHandler msgProcessorHandler() {
        return new MsgProcessorHandler();
    }

    @Bean
    NettyServer nettyServer(MsgProcessorHandler msgProcessorHandler) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(nettyServerProperty.getPort(), msgProcessorHandler);
        return nettyServer;
    }

    @Bean
    ConnectionManager connectionManager(NettyServer nettyServer) {
        return new ConnectionManager(nettyServer.getConnMap());
    }

}
