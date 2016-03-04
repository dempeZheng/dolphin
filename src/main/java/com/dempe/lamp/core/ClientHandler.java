package com.dempe.lamp.core;

import com.dempe.lamp.client.ReplyFuture;
import com.dempe.lamp.client.ReplyWaitQueue;
import com.dempe.lamp.proto.Response;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * client的消息处理handler,处理服务端返回的消息
 * User: Dempe
 * Date: 2016/1/28
 * Time: 18:47
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandler extends ChannelHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private final static ReplyWaitQueue replyQueue = new ReplyWaitQueue();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Integer id = 0;
        try {
            Response resp = (Response) msg;
            id = resp.getId();
            // 从发送消息队列ReplyWaitQueue take对应的future(消息发送前会将消息放到ReplyWaitQueue)
            ReplyFuture future = replyQueue.take(id);
            if (future == null) {
                return;
            }
            // 唤醒对应的future
            future.onReceivedReply(resp);
        } finally {
            ReferenceCountUtil.release(msg);
            replyQueue.remove((Long.valueOf(id)));
        }
    }


}

