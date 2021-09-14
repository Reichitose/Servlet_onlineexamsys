package com.reiuy.controller;

import com.reiuy.dao.UserDao;
import com.reiuy.entity.Users;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/***
 * 此处采用了利用session给登录的合法用户发放令牌的机制
 * 确保用户是通过登录访问动态资源文件而非直接通过地址访问
 * 而实际情况下，这种机制并不合理
 *
 * 问题如下：
 * 1.开发难度较大，每个提供服务的Servlet都需要编写关于session的判断
 * 2.只能对动态资源文件进行保护，并不能保护静态资源文件
 * 存在更优的采用过滤器的机制
 */




public class UserFindServlet extends jakarta.servlet.http.HttpServlet {

    protected void doGet(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) throws jakarta.servlet.ServletException, IOException {


        UserDao dao = new UserDao();
        PrintWriter out;
/*    -----------注释掉未优化的部分，实际功能通过过滤器实现-------------
        //索要当前用户在服务端的HttpSession
        HttpSession session = request.getSession(false);
        //getSession(false)方法，如果用户有储物柜，返回储物柜，如果没有返回null
        if (session == null) {
            response.sendRedirect("/onlineexamsys/login_error.html");
            return;
        }

        //不为空说明存在储物柜，提供服务
  -----------注释掉未优化的部分，实际功能通过过滤器实现-------------
 */
        //1.调用DAO将查询命令推送到数据库服务器上来得到所有的用户信息
        List<Users> userList = dao.findAll();
        //2.调用相应对象 将用户信息结合table命令以二进制形式写入响应体
        response.setContentType("text/html;charset=utf-8");
        out = response.getWriter();
        out.print("<table border='2' align='center'>");
        out.print("<tr>");
        out.print("<td>用户编号</td>");
        out.print("<td>用户姓名</td>");
        out.print("<td>用户密码</td>");
        out.print("<td>用户性别</td>");
        out.print("<td>用户邮箱</td>");
        out.print("<td>操作</td>");
        out.print("</tr>");
        for (Users users : userList) {
            out.print("<tr>");
            out.print("<td>" + users.getUserId() + "</td>");
            out.print("<td>" + users.getUserName() + "</td>");
            out.print("<td>******</td>");
            out.print("<td>" + users.getSex() + "</td>");
            out.print("<td>" + users.getEmail() + "</td>");
            out.print("<td><a href='/onlineexamsys/user/delete?userId=" + users.getUserId() + "'>删除用户</a></td>");
            out.print("</tr>");
        }
        out.print("</table>");

    }
}

