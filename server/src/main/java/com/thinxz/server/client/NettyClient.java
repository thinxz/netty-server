package com.thinxz.server.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * 客户端实体类
 *
 * @author thinxz 2018-03-01
 */
@Log4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NettyClient {

    /**
     * 客户端地址
     */
    private String address;
    /**
     * 连接信息
     */
    private Connection connection;

}
