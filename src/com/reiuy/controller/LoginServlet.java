package com.reiuy.controller;

import com.reiuy.dao.UserDao;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginServlet extends jakarta.servlet.http.HttpServlet {
    protected void doPost(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) throws jakarta.servlet.ServletException, IOException {
        String userName,password;
        UserDao dao = new UserDao();
        int result = 0;


        //1.调用请求对象对请求体使用utf-8进行重新编辑
        request.setCharacterEncoding("utf-8");


        //2.调用请求对象读取请求体信息

        userName = request.getParameter("userName");
        password = request.getParameter("password");


        //3.调用dao将查询验证信息推送到数据库服务器上

        result = dao.login(userName,password);


        //4.使用响应对象根据验证结果将不同资源写入响应头，交还给浏览器
        if(result == 1){
            //判定来访用户合法后通过请求对象向TomCat为当前用户申请一个私人储物柜
            HttpSession session = request.getSession();
            response.sendRedirect("/onlineexamsys/index.html");

        }else{
            response.sendRedirect("/onlineexamsys/login_error.html");
        }


    }
}
