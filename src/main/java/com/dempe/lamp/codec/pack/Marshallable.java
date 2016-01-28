package com.dempe.lamp.codec.pack;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public interface Marshallable {

    public abstract void marshal(Pack pack);

    public abstract void unmarshal(Unpack unpack);
}