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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2015/12/11
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
public class CommonClient implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonClient.class);

    protected Bootstrap b;

    protected ChannelFuture f;

    protected Channel channel;

    protected EventLoopGroup group;

    private DefaultEventExecutorGroup executorGroup;

    private String host;

    private int port;

    private long connectTimeout = 5000L;

    protected Map<Integer, Context> contextMap = new ConcurrentHashMap<Integer, Context>();

    public static class Context {
        final Request request;
        private final short id;
        final Callback cb;

        Context(int id, Request request, Callback cb) {
            this.id = (short) id;
            this.cb = cb;
            this.request = request;
        }
    }


    public CommonClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
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

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    closeSync();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }));
        connect(host, port);
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


    public void connect(final String host, final int port) {
        try {
            f = b.connect(host, port).sync();
            channel = f.channel();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    public void closeSync() throws IOException {
        try {
            f.channel().close().sync();
            group.shutdownGracefully();

        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void close() {
        if (isConnected()) {
            f.channel().close();
        }
    }

    public void send(Request request) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        if (!isConnected()) {
            reconnect();
        }
        f.channel().writeAndFlush(request);
    }

    public boolean reconnect() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        close();
        LOGGER.info("start reconnect to server.");
        f = b.connect(host, port);// 异步建立长连接
        f.get(connectTimeout, TimeUnit.MILLISECONDS); // 最多等待5秒，如连接建立成功立即返回
        LOGGER.info("end reconnect to server result:" + isConnected());
        return isConnected();
    }

    public boolean isConnected() {
        return f != null && f.channel().isActive();
    }


}
