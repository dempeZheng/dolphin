package com.dempe.lamp.utils.pack;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public interface Marshallable {

    /**
     * 消息编码类
     *
     * @param pack
     */
    void marshal(Pack pack);

    /**
     * 消息编码类
     *
     * @param unpack
     */
    void unmarshal(Unpack unpack) throws IOException;
}