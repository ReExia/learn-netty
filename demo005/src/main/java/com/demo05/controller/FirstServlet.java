package com.demo05.controller;

import com.demo05.mytomcat.http.MyHttpRequest;
import com.demo05.mytomcat.http.MyResponse;
import com.demo05.mytomcat.http.MyServlet;

public class FirstServlet extends MyServlet {

    @Override
    public void doGet(MyHttpRequest request, MyResponse myResponse) {
        String param = "name";
        String str = request.getParamter(param);
        myResponse.write(param + ":" + str,200);
    }

    @Override
    public void doPost(MyHttpRequest request, MyResponse myResponse) {
        String param = "name";
        String str = request.getParamter(param);
        myResponse.write(param + ":" + str,200);
    }
}
