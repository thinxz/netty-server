package com.thinxz.server.server;

import com.thinxz.server.client.NettyClient;
import lombok.Data;

import java.util.Map;

/**
 * 连接管理器
 *
 * @author thinxz
 */
@Data
public class ConnectionManager {

    /**
     * 所有连接信息
     */
    private final Map<String, NettyClient> connMap;

    public ConnectionManager(Map<String, NettyClient> connMap) {
        this.connMap = connMap;
    }

}
