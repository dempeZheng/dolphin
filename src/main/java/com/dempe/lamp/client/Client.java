package com.dempe.lamp.client;

import com.dempe.lamp.proto.Request;
import com.dempe.lamp.proto.Response;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/25
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public interface Client {


    public void sendOnly(Request request) throws Exception;

    /**
     * 发送消息，并等待Response
     *
     * @param request
     * @return Response
     */
    public Callback call(Request request, Callback callback) throws Exception;

    public Future<Response> send(Request request) throws Exception;


    public Response sendAnWait(Request request) throws Exception;

    public Response sendAnWait(Request request, long amount, TimeUnit unit) throws Exception;


}
