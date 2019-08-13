package com.baffalotech.integration.http.netty.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 一个默认的servlet
 *
 * @author acer01
 *  2018/7/15/015
 */
public class NettyDefaultHttpServlet extends HttpServlet {

	private static final long serialVersionUID = 7528926083421604250L;

	@Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AsyncContext context = request.startAsync();
        context.start(()->{
            try {
                response.getWriter().write("默认的servlet");
                context.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
