package com.dempe.lamp.client;


import com.dempe.lamp.codec.IDMessageDecoder;
import com.dempe.lamp.codec.MarshallableEncoder;
import com.dempe.lamp.proto.IDMessage;
import com.dempe.lamp.proto.Request;
import com.dempe.lamp.proto.Response;
import com.dempe.lamp.utils.pack.Unpack;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * TODO【严重】 消息发送超时情况，contextMap内上下文对象无法清除，存在内存溢出的风险 待添加定时任务，定期清除超时的contextMap，
 *
 * User: Dempe
 * Date: 2015/12/11
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
public class CommonClient implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonClient.class);

    protected Bootstrap b;

    protected EventLoopGroup group;
    protected ChannelPool channelPool;

    protected Map<Integer, Context> contextMap = new ConcurrentHashMap<Integer, Context>();
    private DefaultEventExecutorGroup executorGroup;
    private String host;
    private int port;
    private int nextMessageId = 1;

    public CommonClient(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        init();
    }

    private int getNextMessageId() {
        int rc = nextMessageId;
        nextMessageId++;
        if (nextMessageId == 0) {
            nextMessageId = 1;
        }
        return rc;
    }

    private void init() throws InterruptedException {
        b = new Bootstrap();
        group = new NioEventLoopGroup(4);
        executorGroup = new DefaultEventExecutorGroup(4,
                new DefaultThreadFactory("worker group"));
        b.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        initClientChannel(ch);
                    }
                });

        channelPool = new ChannelPool(this);
    }

    public void initClientChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("RequestEncoder", new MarshallableEncoder())
                .addLast("ResponseDecoder", new IDMessageDecoder())
                .addLast("ClientHandler", new ChannelHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        Integer id = 0;
                        IDMessage message = (IDMessage) msg;
                        id = message.getMessageID();
                        byte[] bytes = message.getBytes();
                        Unpack unpack = new Unpack(bytes);
                        Response resp = new Response();
                        resp.unmarshal(unpack);
                        Context context = contextMap.remove(id);
                        if (context == null) {
                            LOGGER.debug("messageID:{}, take Context null", id);
                            return;
                        }
                        context.cb.onReceive(resp);
                    }
                });
    }

    public ChannelFuture connect() {
        return b.connect(host, port);

    }

    public ChannelFuture connect(final String host, final int port) {
        ChannelFuture f = null;
        try {
            f = b.connect(host, port).sync();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return f;
    }

    public void sendOnly(Request request) throws Exception {
        writeAndFlush(request);
    }

    /**
     * 发送消息，并等待Response
     *
     * @param request
     * @return Response
     */
    public Callback call(Request request, Callback callback) throws Exception {
        int id = getNextMessageId();
        request.setMessageID(id);
        Context context = new Context(id, request, callback);
        contextMap.put(id, context);
        writeAndFlush(request);
        return callback;
    }

    public Future<Response> send(Request request) throws Exception {
        Promise<Response> future = new Promise<Response>();
        call(request, future);
        return future;
    }

    public Response sendAnWait(Request request) throws Exception {
        Future<Response> future = send(request);
        return future.await();
    }

    public Response sendAnWait(Request request, long amount, TimeUnit unit) throws Exception {
        Future<Response> future = send(request);
        return future.await(amount, unit);
    }

    public void writeAndFlush(Object request) throws Exception {
        Connection connection = channelPool.getChannel();
        connection.doTransport(request);
    }

    public static class Context {
        final Request request;
        final Callback cb;
        private final short id;

        Context(int id, Request request, Callback cb) {
            this.id = (short) id;
            this.cb = cb;
            this.request = request;
        }
    }

}
