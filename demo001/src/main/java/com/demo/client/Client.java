package com.demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

    public static void main(String[] args) {

        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });

            ChannelFuture f = bootstrap.connect("127.0.0.1",8000).sync();
            f.channel().writeAndFlush(Unpooled.copiedBuffer(new String("hello server").getBytes()));
            f.channel().writeAndFlush(Unpooled.copiedBuffer(new String("hello server").getBytes()));
            f.channel().writeAndFlush(Unpooled.copiedBuffer(new String("hello server").getBytes()));
            f.channel().writeAndFlush(Unpooled.copiedBuffer(new String("hello server").getBytes()));
            f.channel().writeAndFlush(Unpooled.copiedBuffer(new String("hello server").getBytes()));
            f.channel().writeAndFlush(Unpooled.copiedBuffer(new String("hello server").getBytes()));
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
        }


    }

}
