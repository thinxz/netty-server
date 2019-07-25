package com.thinxz.server.handler;

import com.sinoxx.sserver.parser.util.ToolBuff;
import com.thinxz.common.module.entity.T808Message;
import com.thinxz.server.server.ConnectionManager;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消息业务处理服务
 *
 * @author thinxz 2019-02-28
 */
@Log4j
public class MsgProcessorHandler {

    @Autowired
    private ConnectionManager connectionManager;

    public void process(T808Message msg) {
        log.info(String.format("业务解析消息 => [ %s - %s ]", msg.getOnlineNo(), msg));

        // 反向发送回去
        connectionManager.getConnMap().get(msg.getOnlineNo()).getConnection().getChannel().writeAndFlush(ToolBuff.decodeHexString(msg.getHexMsg()));
    }

}
