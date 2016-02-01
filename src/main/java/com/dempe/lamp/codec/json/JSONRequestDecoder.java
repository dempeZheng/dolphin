package com.dempe.lamp.codec.json;

import com.dempe.lamp.codec.AbstractDecoder;
import com.dempe.lamp.proto.json.JSONRequest;
import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Unpack;

/**
 * Request请求解码类
 * User: Dempe
 * Date: 2015/12/10
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class JSONRequestDecoder extends AbstractDecoder {

    @Override
    public Marshallable decode(Unpack unpack) {
        JSONRequest proto = new JSONRequest();
        proto.unmarshal(unpack);
        return proto;
    }
}
