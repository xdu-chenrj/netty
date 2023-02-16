package org.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.codec.NettyKryoDecoder;
import org.codec.NettyKryoEncoder;
import org.entity.RpcRequest;
import org.entity.RpcResponse;
import org.serialize.KryoSerializer;

import java.net.InetSocketAddress;

@Slf4j
public class NettyClient {
    private static final Bootstrap bootstrap;

    static {
        bootstrap = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        bootstrap.group(new NioEventLoopGroup()).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) {
                nioSocketChannel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                nioSocketChannel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                nioSocketChannel.pipeline().addLast(new NettyClientHandler());
            }
        });
    }

    public RpcResponse sendMessage(RpcRequest rpcRequest) {
        try {
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 8888)).sync();
            log.info("connect success");
            Channel channel = channelFuture.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("client send message: {}", rpcRequest.toString());
                    } else {
                        log.error("client send message failed, ", future.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return channel.attr(key).get();
            }
        } catch (Exception e) {
            log.error("occur exception when connect server:", e);
        }
        return null;
    }

    public static void main(String[] args) {
        RpcRequest rpcRequest = RpcRequest.builder().interfaceName("interface").methodName("method").build();
        RpcResponse rpcResponse = new NettyClient().sendMessage(rpcRequest);
        assert rpcResponse != null;
        log.info("rpcResponse: {}", rpcResponse.getMessage());
    }

}
