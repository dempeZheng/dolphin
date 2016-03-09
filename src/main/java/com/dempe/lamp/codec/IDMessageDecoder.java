package com.dempe.lamp.codec;

import com.dempe.lamp.proto.IDMessage;
import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Unpack;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/3/9
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class IDMessageDecoder extends AbstractDecoder {


    @Override
    public Marshallable decode(Unpack unpack) throws IOException {
        IDMessage message = new IDMessage();
        message.unmarshal(unpack);
        return message;
    }
}
