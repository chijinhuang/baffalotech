package com.baffalotech.integration.http.netty.servlet.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.http.netty.core.util.ExceptionUtil;
import com.baffalotech.integration.http.netty.servlet.NettyHttpServletRequest;
import com.baffalotech.integration.http.netty.servlet.NettyHttpServletResponse;
import com.baffalotech.integration.http.netty.servlet.NettyServletRequestDispatcher;
import com.baffalotech.integration.http.netty.servlet.util.ServletUtil;

/**
 * 错误页管理
 * @author 84215
 */
public class NettyServletErrorPageManager {

    private static final Logger logger = LoggerFactory.getLogger(NettyServletErrorPageManager.class);
    private Map<String, NettyServletErrorPage> exceptionPages = new ConcurrentHashMap<>();
    private Map<Integer, NettyServletErrorPage> statusPages = new ConcurrentHashMap<>();

    public void add(NettyServletErrorPage errorPage) {
        String exceptionType = errorPage.getExceptionType();
        if (exceptionType == null) {
            statusPages.put(errorPage.getStatus(), errorPage);
        } else {
            exceptionPages.put(exceptionType, errorPage);
        }
    }

    public void remove(NettyServletErrorPage errorPage) {
        String exceptionType = errorPage.getExceptionType();
        if (exceptionType == null) {
            statusPages.remove(errorPage.getStatus(), errorPage);
        } else {
            exceptionPages.remove(exceptionType, errorPage);
        }
    }

    public NettyServletErrorPage find(int statusCode) {
        return statusPages.get(statusCode);
    }

    public NettyServletErrorPage find(Throwable exceptionType) {
        if (exceptionType == null) {
            return null;
        }
        Class<?> clazz = exceptionType.getClass();
        String name = clazz.getName();
        while (!Object.class.equals(clazz)) {
            NettyServletErrorPage errorPage = exceptionPages.get(name);
            if (errorPage != null) {
                return errorPage;
            }
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                break;
            }
            name = clazz.getName();
        }
        return null;
    }

    /**
     * 处理错误页
     * @param errorPage 错误页
     * @param throwable 错误
     * @param httpServletRequest 请求
     * @param httpServletResponse 响应
     */
    public void handleErrorPage(NettyServletErrorPage errorPage,Throwable throwable, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        if(errorPage == null){
            return;
        }
        

        NettyHttpServletRequest request = ServletUtil.unWrapper(httpServletRequest);
        NettyHttpServletResponse response = ServletUtil.unWrapper(httpServletResponse);

        NettyServletRequestDispatcher dispatcher = request.getRequestDispatcher(errorPage.getPath());
        try {
            if(throwable != null) {
                httpServletRequest.setAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE, throwable.getClass());
            }
            httpServletRequest.setAttribute(RequestDispatcher.ERROR_SERVLET_NAME,dispatcher.getName());
            httpServletRequest.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
            httpServletRequest.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, response.getStatus());
//            httpServletRequest.setAttribute(RequestDispatcher.ERROR_MESSAGE, response.getMessage());

            if (httpServletResponse.isCommitted()) {
                dispatcher.include(request, httpServletResponse);
            } else {
                response.resetBuffer(true);
                httpServletResponse.setContentLength(-1);
                //如果post发生异常，应该重新生成request，然后dispatch
                dispatcher.forward(request, httpServletResponse);

                response.getOutputStream().setSuspendFlag(false);
            }
        } catch (Throwable e) {
            logger.error("on handleErrorPage error. url="+request.getRequestURL()+", case="+e.getMessage(),e);
            ExceptionUtil.handleThrowable(e);
        }
    }
}
