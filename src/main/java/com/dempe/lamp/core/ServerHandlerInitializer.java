package com.dempe.lamp.core;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2015/11/3
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
public class ServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private ServerContext context;

    public ServerHandlerInitializer(ServerContext context) {
        this.context = context;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("RequestDecoder", context.getDecoder().newInstance())
                .addLast("ResponseEncoder", context.getEncoder().newInstance())
                .addLast("dispatcher", new ProcessorHandler(context))
        ;
    }
}
