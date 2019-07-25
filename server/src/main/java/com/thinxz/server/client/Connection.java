package com.thinxz.server.client;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 连接实体类
 *
 * @author thinxz 2019-02-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Connection {

    /**
     * 终端标识
     */
    private String onlineNo;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 在线时间
     */
    private Date onlineDate;
    /**
     * 接收到的数据包数量
     */
    private int packageNum;
    /**
     * 断开次数
     */
    private int disconnectTimes;
    /**
     * 错误包数
     */
    private int errorPacketNum;
    /**
     * 是否连接成功 - 终端有消息时判断设置TRUE , TCP连接移除时 FALSE
     */
    private boolean connected;
    /**
     * 会话Id
     */
    private long sessionId;
    /**
     * 会话
     */
    private Channel channel;

}
