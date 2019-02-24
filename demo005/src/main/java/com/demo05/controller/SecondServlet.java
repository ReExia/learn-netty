package com.demo05.controller;

import com.alibaba.fastjson.JSON;
import com.demo05.mytomcat.http.MyHttpRequest;
import com.demo05.mytomcat.http.MyResponse;
import com.demo05.mytomcat.http.MyServlet;

public class SecondServlet extends MyServlet {

    @Override
    public void doGet(MyHttpRequest request, MyResponse response) {
        doPost(request, response);
    }

    @Override
    public void doPost(MyHttpRequest request, MyResponse response) {
        String str = JSON.toJSONString(request.getParameters(),true);
        response.write(str,200);
    }

}
