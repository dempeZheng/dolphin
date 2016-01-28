package com.dempe.lamp.client;

import com.dempe.lamp.proto.Request;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/25
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public interface Client {

    public void sendOnly(Request request);

    public boolean reconnect() throws IOException, InterruptedException, ExecutionException, TimeoutException;

    public boolean isConnected();

    public void close();


}
