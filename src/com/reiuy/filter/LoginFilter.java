package com.reiuy.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 *
 * 通过过滤器的加强功能将Session的判断放置到过滤器中
 * 且过滤器的作用域为网站下全部的文件
 * 使得每次请求资源文件（包括静态和动态）提供服务时，都需要检查session
 * 实现了阻止恶意登录的功能
 *
 *
 * 虽然但是
 * 由于将loginFilter的作用域参数设置为/*
 * 会将login页面同样进行过滤处理，
 * 而用户将在没有session的情况下被要求session以判断其访问合法性
 * 相当于焊死了所有的门 以没有人能进入的代价保证了进入的安全
 * 并不可取
 *
 *
 * 那么怎么解决呢
 * 我们在过滤器中对判定条件进行修改
 * 我们通过获取uri来确认用户访问的是哪个资源文件
 * 如果是带有login关键字的资源文件，例如login.html   LoginServlet
 * 那么我们将无条件放行
 * 还有一种情况，当用户通过默认欢迎页资源访问login资源文件时，也无条件放行
 *
 * 如果访问的是其他资源文件
 * 那么则对用户的session进行要求并判断其访问的合法性
 * 有则放行，无则返回
 *
 * 这样就实现了防止恶意访问的功能原理
 *
 *
 *
 *
 *
 */

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = null;


        //1.调用请求对象读取当前请求包中请求行中的uri来了解用户访问的资源文件是哪个
        String uri = request.getRequestURI();   //[/网站名/资源文件] 例如 /onlineexamsys/login.html or /onlineexamsys/login_error.html
        //2.如果本次请求的资源文件与登录相关，，例如；login.html或者LoginServlet那么无条件放行
        if(uri.indexOf("login")!=-1 || "/onlineexamsys/".equals(uri)){
            //indexof将返回当前内容login在字符串uri之间第一次出现的位置，如果未出现则返回-1
            //使得我们可以判断需要请求的资源文件中是否出现了login这样的标识性字段
            //通过或||来判断用户是否是通过默认欢迎页访问，需要加上第二个条件
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //3.如果本次请求访问的是其他的资源文件，则需要得到服务器的Session
        session = request.getSession(false);
        //判断来访用户的身份合法性，即session是否为空
        if(session != null){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }//session不为null说明返回了session即不是恶意访问
        request.getRequestDispatcher("/login_error.html").forward(servletRequest,servletResponse);
        //否则为非法请求，发送失败页面



    }


    /* ======================本段代码需要被重写===========================
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //1.由于getSession方法来自于ServletRequest的子类HttpServletRequest
        //当需要调用时，需要强行转换
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //如果之后需要调用redirect则需要强制转换response,此处采用RequestDispatcher
        //HttpServletResponse response = (HttpServletResponse)servletResponse;
        //2..拦截后，通过请求对象所要当前用户的HttpSession
        HttpSession session = request.getSession(false);
        //3.判断来访用户的身份合法性，即session是否为空
        if(session == null){
            request.getRequestDispatcher("/login_error.html");
            //利用请求转发将error界面塞入响应体
            return;
        }
        //合法即放行
        filterChain.doFilter(servletRequest,servletResponse);
        //此时还需去web.xml配置文件中配置过滤器，参数为/*即全部文件

    }
 */
}
