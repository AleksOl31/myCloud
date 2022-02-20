package ru.alexanna.cloud.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class BaseNettyClient {

    public BaseNettyClient() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // maxObjectSize - 1 Gb setup
                        pipeline.addLast(new ObjectDecoder(1048576 * 1000, ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ClientHandler());
                    }
                });
            ChannelFuture future = bootstrap.connect("localhost", 8189).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new BaseNettyClient();
    }
}
