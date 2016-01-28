package com.dempe.lamp.client;

import com.dempe.lamp.proto.Request;
import com.dempe.lamp.proto.Response;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
public class ReplyClient extends CommonClient {

    private static AtomicInteger idMaker = new AtomicInteger(0);

    private ReplyWaitQueue replyQueue = new ReplyWaitQueue();


    public ReplyClient(String host, int port) {
        super(host, port);
    }


    public void sendOnly(Request request) {
        int id = idMaker.incrementAndGet();
        request.setId(id);
        send(request);
    }


    public Response sendAndWait(Request request) {
        int id = idMaker.incrementAndGet();
        System.out.println("id:" + id);
        request.setId(id);
        try {
            ReplyFuture future = new ReplyFuture(id);
            replyQueue.add(future);
            send(request);
            return future.getReply();
        } finally {
            replyQueue.remove(id);
        }

    }

    public Response sendAndWait(Request request, long timeout) {
        int id = idMaker.incrementAndGet();
        request.setId(id);
        try {
            ReplyFuture future = new ReplyFuture(id);
            replyQueue.add(future);
            future.setReadTimeoutMillis(timeout);
            send(request);
            return future.getReply();
        } finally {
            replyQueue.remove(id);
        }

    }
}
