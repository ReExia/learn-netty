package com.demo05.mytomcat.server;

import com.demo05.mytomcat.handler.MyHttpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.apache.log4j.Logger;

public class HttpServer {

    private static final Logger LOG = Logger.getLogger(HttpServer.class);

    public void start(int port) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //服务端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            socketChannel.pipeline().addLast(new HttpResponseEncoder());
                            //服务端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            socketChannel.pipeline().addLast(new HttpRequestDecoder());
                            socketChannel.pipeline().addLast(new MyHttpHandler());
                        }
                    });

            //绑定服务端口
            ChannelFuture future = bootstrap.bind(port).sync();
            LOG.info("HTTP服务已启动，监听端口:" + port);
            //开始接收客户
            future.channel().closeFuture().sync();
        } finally {
            // 优雅的方式关闭线程池
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    public static void main(String [] args){
        try {
            new HttpServer().start(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
