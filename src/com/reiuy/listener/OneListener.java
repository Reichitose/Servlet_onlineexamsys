package com.reiuy.listener;

import com.reiuy.util.JDBCUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OneListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //在tomcat启动时预先创建Connection，在userDao执行时将创建好的Connection交给add

        JDBCUtil util = new JDBCUtil();
        Map map = new HashMap();

        for (int i = 1; i <= 20; i++) {
            Connection con = util.creatCon();
            System.out.println("在Http服务器启动时创建了Connection" + con);
            map.put(con, true); //true表示通道处于空闲，false表示通道正在被使用
        }
        ServletContext application = sce.getServletContext();
        application.setAttribute("key1", map);
    }//此时，作为局部变量的map会被销毁，所以此时将map交付给生命周期最长的全局作用域对象

    //只要服务器不关闭，这20个Connection就不会被销毁
    //在Http服务器对象关闭时，要销毁这20个Connection
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext application = sce.getServletContext();
        Map map = (Map) application.getAttribute("key1");
        Iterator it = map.keySet().iterator();
        //由于set集合的值是无序的，无法遍历需要使用迭代器将其有序化
        while (it.hasNext()) {
            Connection con = (Connection) it.next();
            if (con != null) {
                System.out.println("con" + con + "被销毁");
            }
        }
    }
}
