package org.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.codec.NettyKryoDecoder;
import org.codec.NettyKryoEncoder;
import org.entity.RpcRequest;
import org.entity.RpcResponse;
import org.serialize.KryoSerializer;

public class NettyServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        nioSocketChannel.pipeline().addLast(new NettyKryoDecoder(new KryoSerializer(), RpcRequest.class));
                        nioSocketChannel.pipeline().addLast(new NettyKryoEncoder(new KryoSerializer(), RpcResponse.class));
                        nioSocketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                }).bind(8888);
    }
}
