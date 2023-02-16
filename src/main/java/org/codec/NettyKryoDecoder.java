package org.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.serialize.Serializer;

import java.util.List;

@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private final Serializer serializer;
    private final Class<?> genericClass;
    private static final int BODY_LENGTH = 4;

    public NettyKryoDecoder(Serializer serializer, Class<?> genericClass) {
        this.serializer = serializer;
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() >= BODY_LENGTH) {
            byteBuf.markReaderIndex();
            int dataLength = byteBuf.readInt();
            int bytesLength = byteBuf.readableBytes();
            log.info("data length: {}", dataLength);
            log.info("readableBytes: {}", bytesLength);
            if (dataLength <= 0 || bytesLength <= 0) {
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            if (byteBuf.readableBytes() < dataLength) {
                byteBuf.resetReaderIndex();
                return;
            }
            byte[] body = new byte[dataLength];
            byteBuf.readBytes(body);
            Object o = serializer.deserialize(body, genericClass);
            list.add(o);
            log.info("successful decode byteBuf to Object");
        }
    }
}
