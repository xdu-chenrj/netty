package org.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.codec.NettyKryoDecoder;
import org.codec.NettyKryoEncoder;
import org.entity.RpcRequest;
import org.entity.RpcResponse;
import org.serialize.KryoSerializer;

@NoArgsConstructor
public class NettyServer {
    private static final ServerBootstrap serverBootstrap;

    static {
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        nioSocketChannel.pipeline().addLast(new NettyKryoDecoder(new KryoSerializer(), RpcRequest.class));
                        nioSocketChannel.pipeline().addLast(new NettyKryoEncoder(new KryoSerializer(), RpcResponse.class));
                        nioSocketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                });
    }

    public void run(int port) {
        serverBootstrap.bind(port);
    }

    public static void main(String[] args) {
        new NettyServer().run(8888);
    }
}
