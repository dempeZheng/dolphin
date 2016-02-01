package com.dempe.lamp.codec.json;

import com.dempe.lamp.codec.AbstractEncoder;
import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Pack;

/**
 * Request消息编码类
 * User: Dempe
 * Date: 2015/12/10
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class JSONRequestEncoder extends AbstractEncoder {


    @Override
    public Pack encode(Marshallable request) {
        Pack pack = new Pack();
        request.marshal(pack);
        return pack;
    }

}
