package com.thinxz.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务端配置
 *
 * @author thinxz 2019-07-25
 */
@Data
@ConfigurationProperties(prefix = "netty.server")
public class NettyServerProperty {

    /**
     * 监听端口
     */
    private int port = 8888;
}
