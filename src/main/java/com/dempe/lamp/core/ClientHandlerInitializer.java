package com.dempe.lamp.core;


import com.dempe.lamp.codec.RequestDecoder;
import com.dempe.lamp.codec.RequestEncoder;
import com.dempe.lamp.codec.ResponseDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2015/12/11
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandlerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("RequestEncoder", new RequestEncoder())
                .addLast("ResponseDecoder", new ResponseDecoder());
    }
}
