package com.dempe.lamp.codec;

import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.ProtocolValue;
import com.dempe.lamp.utils.pack.Unpack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/2/1
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {

        // 设置小端模式
        buf = buf.order(ByteOrder.LITTLE_ENDIAN);
        int length = buf.readableBytes();
        if (length < 4) {
            return;
        }
        //
        int firstIntValue = buf.markReaderIndex().readInt();
        int[] protoValue = ProtocolValue.parse(firstIntValue);
        int curPacketSize = protoValue[1];
        int dataSize = curPacketSize - 4;
        length = buf.readableBytes();
        if (length < dataSize) {
            buf.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[dataSize];
        buf.readBytes(bytes, 0, bytes.length);
        Unpack unpack = new Unpack(bytes);
        byte protoType = (byte) protoValue[0];

        if (protoType == 0) {
            Marshallable proto = decode(unpack);
            list.add(proto);
        }

    }

    public abstract Marshallable decode(Unpack unpack) throws IOException;

}
