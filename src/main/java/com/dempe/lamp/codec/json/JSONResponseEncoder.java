package com.dempe.lamp.codec.json;

import com.dempe.lamp.codec.AbstractEncoder;
import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Pack;

/**
 * Response消息编码类
 * User: Dempe
 * Date: 2015/12/10
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class JSONResponseEncoder extends AbstractEncoder {

    @Override
    public Pack encode(Marshallable response) {
        Pack pack = new Pack();
        response.marshal(pack);
        return pack;
    }
}
