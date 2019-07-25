package com.thinxz.server.handler;

import com.thinxz.common.module.entity.T808Message;
import com.thinxz.server.client.Connection;
import com.thinxz.server.client.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;
import java.util.Map;

/**
 * 消息处理, 建立连接会话
 *
 * @author thinxz 2018-02-28
 */
@Log4j
@ChannelHandler.Sharable
public class ProcessorHandler extends SimpleChannelInboundHandler<T808Message> {

    /**
     * 保存在Netty会话中的KEY
     */
    private static AttributeKey<String> key = AttributeKey.valueOf("onlineNo");
    /**
     * 消息怕派发服务类
     */
    @Setter
    private MsgProcessorHandler msgProcessorHandler;
    /**
     * 存放所有链接的映射
     */
    private final Map<String, NettyClient> connMap;

    public ProcessorHandler(Map<String, NettyClient> connMap) {
        this.connMap = connMap;
    }

    /**
     * 读取数据 ， 客户端向服务端发来数据，每次都会回调此方法，表示有数据可读
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T808Message msg) throws Exception {
        if (msg == null || Strings.isBlank(msg.getOnlineNo())) {
            // 无效消息
            log.error(String.format("传送消息无效 => %s ", msg));
            return;
        }

        // 刷新连接
        refreshConnection(ctx, 0, msg);

        // 派发业务处理
        msgProcessorHandler.process(msg);
    }

    /**
     * 获取并创建连接对象
     */
    private void refreshConnection(ChannelHandlerContext ctx, long sessionId, T808Message msg) {
        Channel incoming = ctx.channel();
        incoming.attr(key).set(msg.getOnlineNo());

        NettyClient nettyClient = connMap.get(msg.getOnlineNo());

        if (nettyClient == null || nettyClient.getConnection() == null) {
            log.info(String.format("客户端连接 => %s", incoming.remoteAddress().toString()));
            // 新建客户端并缓存
            nettyClient =
                    NettyClient
                            .builder()
                            .address(incoming.remoteAddress().toString().replace("/", ""))
                            .connection(
                                    Connection
                                            .builder()
                                            .onlineNo(msg.getOnlineNo())
                                            .onlineDate(new Date())
                                            .onlineDate(new Date())
                                            .packageNum(0)
                                            .disconnectTimes(0)
                                            .errorPacketNum(0)
                                            .connected(true)
                                            .sessionId(sessionId)
                                            .channel(incoming)
                                            .build()
                            )
                            .build();

            connMap.put(msg.getOnlineNo(), nettyClient);
        }

        // 刷新连接信息
        Connection conn = nettyClient.getConnection();
        if (conn.getPackageNum() >= Integer.MAX_VALUE) {
            // 清空连接记数数据
            conn.setPackageNum(0);
            conn.setErrorPacketNum(0);
            conn.setErrorPacketNum(0);
        }
        conn.setConnected(true);
        conn.setChannel(incoming);
        // 定位包计数
        conn.setPackageNum(conn.getPackageNum() + 1);
        conn.setOnlineDate(new Date());
        conn.setSessionId(sessionId);
    }

    /**
     * Netty TCP 连接异常 , 及连接的释放流程 : exceptionCaught -> channelInactive -> channelUnregistered
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }

    /**
     * handlerAdded() -> channelRegistered() -> channelActive() -> channelRead() -> channelReadComplete()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {

    }

    /**
     * TCP 的建立
     * channel 的所有的业务逻辑链准备完毕（也就是说 channel 的 pipeline 中已经添加完所有的 handler）以及绑定好一个 NIO 线程之后，这条连接算是真正激活了，接下来就会回调到此方法。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    /**
     * TCP的释放，表明这条连接已经被关闭了，这条连接在 TCP 层面已经不再是 ESTABLISH 状态了
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

    }

    /**
     * 表明与这条连接对应的 NIO 线程移除掉对这条连接的处理
     * channelInactive() -> channelUnregistered() -> handlerRemoved()
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {

    }

}
