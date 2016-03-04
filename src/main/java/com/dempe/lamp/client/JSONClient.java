package com.dempe.lamp.client;

import com.dempe.lamp.codec.json.JSONRequestEncoder;
import com.dempe.lamp.codec.json.JSONResponseDecoder;
import com.dempe.lamp.core.ClientHandler;
import com.dempe.lamp.proto.Request;
import com.dempe.lamp.proto.Response;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 有返回消息的client实现
 * User: Dempe
 * Date: 2016/1/28
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
public class JSONClient extends CommonClient {

    // 消息id生成器，消息id用户标识消息，标识sendAndWait方法的返回
    private static AtomicInteger idMaker = new AtomicInteger(0);

    // 消息返回会被装入到队列里
    private ReplyWaitQueue replyQueue = new ReplyWaitQueue();


    public JSONClient(String host, int port) {
        super(host, port);
    }

    /**
     * 指定json编码
     *
     * @param ch
     */
    @Override
    public void initClientChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("RequestEncoder", new JSONRequestEncoder())
                .addLast("ResponseDecoder", new JSONResponseDecoder())
                .addLast("ClientHandler", new ClientHandler());
    }

    /**
     * 仅仅发送消息，不关心返回
     *
     * @param request
     */
    public void sendOnly(Request request) {
        int id = idMaker.incrementAndGet();
        request.setId(id);
        send(request);
    }


    /**
     * 发送消息，并等待Response
     *
     * @param request
     * @return Response
     */
    public Response sendAndWait(Request request) {
        int id = idMaker.incrementAndGet();
        request.setId(id);
        ReplyFuture future = new ReplyFuture(id);
        replyQueue.add(future);
        send(request);
        return future.getReply();
    }

    public ReplyFuture sendAndWaitNew(Request request) {
        int id = idMaker.incrementAndGet();
        request.setId(id);
        ReplyFuture future = new ReplyFuture(id);
        replyQueue.add(future);
        send(request);
        return future;
    }


    /**
     * 发送消息，指定超时时间，等待Response
     *
     * @param request 请求消息
     * @param timeout 超时时间
     * @return Response
     */
    public Response sendAndWait(Request request, long timeout) {
        int id = idMaker.incrementAndGet();
        request.setId(id);
        ReplyFuture future = new ReplyFuture(id);
        replyQueue.add(future);
        future.setReadTimeoutMillis(timeout);
        send(request);
        return future.getReply();

    }
}
