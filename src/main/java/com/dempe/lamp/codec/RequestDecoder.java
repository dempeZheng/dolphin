package com.dempe.lamp.codec;

import com.dempe.lamp.codec.pack.ProtocolValue;
import com.dempe.lamp.codec.pack.Unpack;
import com.dempe.lamp.proto.json.JSONRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;
import java.util.List;

/**
 * Request请求解码类
 * User: Dempe
 * Date: 2015/12/10
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class RequestDecoder extends ByteToMessageDecoder {

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
            JSONRequest proto = new JSONRequest();
            proto.unmarshal(unpack);
            list.add(proto);
        }

    }

}
