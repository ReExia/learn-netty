package com.demo05.mytomcat.handler;

import com.demo05.mytomcat.config.CustomConfig;
import com.demo05.mytomcat.http.MyHttpRequest;
import com.demo05.mytomcat.http.MyResponse;
import com.demo05.mytomcat.http.MyServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class MyHttpHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = Logger.getLogger(MyHttpHandler.class);

    private static final Map<Pattern,Class<?>> servletMapping = new HashMap<Pattern,Class<?>>();

    static {
        CustomConfig.load("web.properties");
        for (String key : CustomConfig.getKeys()){
            String name = key.replaceFirst("servlet.", "");
            if (name.indexOf(".") != -1){
                name = name.substring(0, name.indexOf("."));
            }else {
                continue;
            }
            String pattern = CustomConfig.getString("servlet." + name + ".urlPattern");
            pattern = pattern.replaceAll("\\*",".*");
            String className = CustomConfig.getString("servlet." + name + ".className");
            if (!servletMapping.containsKey(pattern)){
                try {
                    servletMapping.put(Pattern.compile(pattern), Class.forName(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("ServletMapping:"+servletMapping);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest){
            HttpRequest r = (HttpRequest) msg;
            MyHttpRequest myHttpRequest = new MyHttpRequest(ctx,r);
            MyResponse myResponse = new MyResponse(ctx,r);

            String uri = myHttpRequest.getUri();
            String method = myHttpRequest.getMethod();

            LOG.info(String.format("Uri : %s, method : %s", uri, method));

            boolean hasPattern = false;

            for (Map.Entry<Pattern, Class<?>> entry : servletMapping.entrySet()) {
                if (entry.getKey().matcher(uri).matches()) {
                    MyServlet servlet = (MyServlet) entry.getValue().newInstance();
                    if("get".equalsIgnoreCase(method)){
                        servlet.doGet(myHttpRequest, myResponse);
                    }else{
                        servlet.doPost(myHttpRequest, myResponse);
                    }
                    hasPattern = true;
                }
                if (!hasPattern){
                    String out = String.format("404 NotFound URL%s for method %s", uri,method);
                    myResponse.write(out,404);
                    return;
                }
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("服务器异常:",cause);
        ctx.close();
    }
}
