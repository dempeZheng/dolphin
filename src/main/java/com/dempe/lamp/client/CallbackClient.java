package com.dempe.lamp.client;

import com.dempe.lamp.proto.Request;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/3/9
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 */
public class CallbackClient extends CommonClient {

    private int nextMessageId = 1;

    private int getNextMessageId() {
        int rc = nextMessageId;
        nextMessageId++;
        if (nextMessageId == 0) {
            nextMessageId = 1;
        }
        return rc;
    }


    public CallbackClient(String host, int port) {
        super(host, port);
    }

    public void sendOnly(Request request) throws Exception {
        send(request);
    }

    /**
     * 发送消息，并等待Response
     *
     * @param request
     * @return Response
     */
    public Callback send(Request request, Callback callback) throws Exception {
        int id = getNextMessageId();
        request.setMessageID(id);
        Context context = new Context(id, request, callback);
        contextMap.put(id, context);
        send(request);
        return callback;
    }


}
