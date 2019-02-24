package com.demo05.mytomcat.http;

public abstract class MyServlet {
    public void doGet(MyHttpRequest request, MyResponse myResponse){};
    public void doPost(MyHttpRequest request, MyResponse myResponse){};
}
