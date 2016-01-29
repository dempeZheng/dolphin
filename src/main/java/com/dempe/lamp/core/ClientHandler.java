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
 * Created with IntelliJ IDEA.
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
        try {
            Response resp = (Response) msg;
            Integer id = resp.id();
            ReplyFuture future = replyQueue.take(id);
            if (future == null) {
                //TODO
                return;
            }
            future.onReceivedReply(resp);
            LOGGER.debug("result = {}", resp.toString());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


}

