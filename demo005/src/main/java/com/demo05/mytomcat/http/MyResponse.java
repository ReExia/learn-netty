package com.demo05.mytomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MyResponse {

    private ChannelHandlerContext ctx;

    private HttpRequest request;
    private static Map<Integer, HttpResponseStatus> statusMap = new HashMap<Integer, HttpResponseStatus>();

    static {
        statusMap.put(200, HttpResponseStatus.OK);
        statusMap.put(404,HttpResponseStatus.NOT_FOUND);
        statusMap.put(500,HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public MyResponse(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public void write(String outString, Integer status){
        try {
            //设置响应投
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    statusMap.get(status),
                    Unpooled.wrappedBuffer(outString.getBytes("UTF-8")));
            response.headers().set(CONTENT_TYPE, "text/json");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(EXPIRES, 0);
            if (HttpHeaders.isKeepAlive(request)){
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            //向浏览器写数据
            ctx.write(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            ctx.flush();
        }
    }
}
