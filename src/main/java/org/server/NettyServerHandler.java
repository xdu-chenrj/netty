package org.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.entity.RpcRequest;
import org.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RpcRequest request = (RpcRequest) msg;
        logger.info("server receive msg: " + request.toString());
        RpcResponse rpcResponse = RpcResponse.builder().message("hello").build();
        ChannelFuture channelFuture = ctx.writeAndFlush(rpcResponse);
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }
}
