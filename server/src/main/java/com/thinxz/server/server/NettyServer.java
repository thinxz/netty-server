package com.thinxz.server.server;

import com.google.common.collect.Maps;
import com.thinxz.common.handler.decoder.MsgDecoder;
import com.thinxz.server.client.NettyClient;
import com.thinxz.server.handler.MsgProcessorHandler;
import com.thinxz.server.handler.ProcessorHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Netty TCP 服务器
 *
 * @Author
 */
@Log4j
public class NettyServer {

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ChannelFuture channelFuture;

    private ServerBootstrap serverBootstrap;

    @Getter
    private Map<String, NettyClient> connMap;

    public void start(int port, MsgProcessorHandler msgProcessorHandler) {
        try {
            // 解析成T808Message消息 - 消息业务派发
            this.connMap = Maps.newHashMap();
            ProcessorHandler processorHandler = new ProcessorHandler(connMap);
            processorHandler.setMsgProcessorHandler(msgProcessorHandler);

            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //超过20分钟未收到客户端消息则自动断开客户端连接 - 平台对客户端的TCP活性检测
                            // Netty 内置 - IdleStateHandler心跳机制 ，检测远端是否存活，如果不存活或活跃则对空闲Socket连接进行处理避免资源的浪费；
                            ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(20, 0, 0, TimeUnit.MINUTES));
                            // 消息编解码
                            ch.pipeline().addLast("decoder", new MsgDecoder());
                            ch.pipeline().addLast("encoder", new ByteArrayEncoder());
                            // 业务处理
                            ch.pipeline().addLast(processorHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, false);

            // 绑定端口，开始接收进来的连接
            channelFuture = serverBootstrap.bind(port).sync();

            log.info(String.format("Netty Server Starting, Listening port [%s] ... ", port));
        } catch (Exception e) {
            log.error(String.format("开启 Netty Server 错误"), e);
        }
    }

    /**
     * 关闭Netty服务
     */
    public void stop() {
        try {
            channelFuture.channel().close();
        } catch (Exception ex) {
        } finally {
            try {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            } catch (Exception ex) {
            }
        }
    }

}
