package com.dempe.lamp.codec;

import com.dempe.lamp.codec.pack.Pack;
import com.dempe.lamp.codec.pack.ProtocolValue;
import com.dempe.lamp.proto.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Response消息编码类
 * User: Dempe
 * Date: 2015/12/10
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class ResponseEncoder extends MessageToByteEncoder<Response> {


    private final static Logger LOGGER = LoggerFactory.getLogger(ResponseEncoder.class);

    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;


    /**
     * @param channelHandlerContext
     * @param response
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Response response, ByteBuf byteBuf) throws Exception {
        try {
            Pack pack = new Pack();
            response.marshal(pack);
            ByteBuffer data = pack.getBuffer();
            byte protoType = 0;
            if (pack.getAttachment() != null) {
                protoType = Byte.parseByte(pack.getAttachment().toString());
            }
            byteBuf = byteBuf.order(byteOrder);// 字节序转成YY协议的低端字节
            byteBuf.writeBytes(getOutBytes(data, protoType));

        } catch (Throwable e) {
            LOGGER.error("throwable: " + e.getMessage(), e);
            throw new EncoderException(e);
        }
    }

    protected byte[] getOutBytes(ByteBuffer data, byte protoType) {
        int len = data.limit() - data.position() + 4;
        ByteBuffer out = ByteBuffer.allocate(len);
        // 设置小端模式
        out.order(byteOrder);
        int nFirstValue = ProtocolValue.combine(len, protoType);
        // 长度包含包长度int 4个字节
        out.putInt(nFirstValue);
        out.put(data);
        out.flip();
        return out.array();


    }
}
