package com.dempe.lamp.codec;

import com.dempe.lamp.proto.Request;
import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Unpack;

/**
 * Request请求解码类
 * User: Dempe
 * Date: 2015/12/10
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class RequestDecoder extends AbstractDecoder {

    @Override
    public Marshallable decode(Unpack unpack) {
        Request proto = new Request();
        proto.unmarshal(unpack);
        return proto;
    }
}
