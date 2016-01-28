package com.dempe.lamp.codec.pack;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class ProtocolValue {

    public static int combine(int len, int protoType) {
        return protoType << 24 | len;
    }

    public static int[] parse(int firstValue) {
        int nProtoType = firstValue >> 24;
        int packetSize = firstValue & 0x00ffffff;
        return new int[]{nProtoType, packetSize};
    }
}
