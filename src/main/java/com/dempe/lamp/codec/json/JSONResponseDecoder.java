package com.dempe.lamp.codec.json;

import com.dempe.lamp.codec.AbstractDecoder;
import com.dempe.lamp.proto.json.JSONResponse;
import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Unpack;

import java.io.IOException;

/**
 * Response消息解码类
 * User: Dempe
 * Date: 2015/12/10
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class JSONResponseDecoder extends AbstractDecoder {


    @Override
    public Marshallable decode(Unpack unpack) throws IOException {
        JSONResponse response = new JSONResponse();
        response.unmarshal(unpack);
        return response;
    }
}
