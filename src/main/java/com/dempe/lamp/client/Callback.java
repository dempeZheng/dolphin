package com.dempe.lamp.client;

import com.dempe.lamp.proto.IDMessage;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/3/9
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
public interface Callback<M> {

    public void onReceive(M message);
}
