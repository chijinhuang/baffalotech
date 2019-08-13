package com.baffalotech.integration.http.netty.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baffalotech.integration.http.netty.core.util.AbstractRecycler;
import com.baffalotech.integration.http.netty.core.util.Recyclable;
import com.baffalotech.integration.http.netty.servlet.pathmap.MappedResource;
import com.baffalotech.integration.http.netty.servlet.util.ServletUtil;

import java.io.IOException;

/**
 * servlet 请求调度
 * @author acer01
 *  2018/7/14/014
 */
public class NettyServletRequestDispatcher implements RequestDispatcher,Recyclable {

    /**
     * 调度路径 (与name字段互斥)
     */
    private String path;
    /**
     * 调度servlet名称 (与path字段互斥)
     */
    private String name;
    /**
     * 过滤链
     */
    private NettyServletFilterChain filterChain;

    private static final AbstractRecycler<NettyServletRequestDispatcher> RECYCLER = new AbstractRecycler<NettyServletRequestDispatcher>() {
        @Override
        protected NettyServletRequestDispatcher newInstance() {
            return new NettyServletRequestDispatcher();
        }
    };

    private NettyServletRequestDispatcher() {}

    public static NettyServletRequestDispatcher newInstance(NettyServletFilterChain filterChain) {
        NettyServletRequestDispatcher instance = RECYCLER.getInstance();
        instance.filterChain = filterChain;
        return instance;
    }

    /**
     * 转发给其他servlet处理 (注:将响应的控制权转移给其他servlet)
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        NettyHttpServletResponse httpResponse = ServletUtil.unWrapper(response);
        if(httpResponse == null){
            throw new UnsupportedOperationException("Not found Original Response");
        }
        HttpServletRequest httpRequest = ServletUtil.unWrapper(request);
        if(httpRequest == null){
            throw new UnsupportedOperationException("Not found Original Request");
        }
        if(response.isCommitted()) {
            throw new IOException("Cannot perform this operation after response has been committed");
        }

        //移交输出流的控制权
        NettyServletOutputStreamWrapper outWrapper = httpResponse.getOutputStream();
        //暂停当前响应
        outWrapper.setSuspendFlag(true);
        //交给下一个servlet
        NettyHttpServletForwardResponse forwardResponse = new NettyHttpServletForwardResponse(httpResponse,outWrapper.unwrap());
        // ServletHttpForwardRequest.class 会将新数据传递下去
        NettyHttpServletForwardRequest forwardRequest = new NettyHttpServletForwardRequest(httpRequest);

        //根据名称
        if (path == null) {
            forwardRequest.setForwardName(name);
            forwardRequest.setPaths(httpRequest.getPathInfo(),httpRequest.getQueryString(),httpRequest.getRequestURI(),httpRequest.getServletPath());
            forwardRequest.setParameterMap(httpRequest.getParameterMap());
        } else {
            forwardRequest.setForwardPath(path);
            //根据路径
            if (forwardRequest.getAttribute(FORWARD_REQUEST_URI) == null) {
                forwardRequest.setAttribute(FORWARD_REQUEST_URI, httpRequest.getRequestURI());
                forwardRequest.setAttribute(FORWARD_CONTEXT_PATH, httpRequest.getContextPath());
                forwardRequest.setAttribute(FORWARD_PATH_INFO, httpRequest.getPathInfo());
                forwardRequest.setAttribute(FORWARD_QUERY_STRING, httpRequest.getQueryString());
                forwardRequest.setAttribute(FORWARD_SERVLET_PATH, httpRequest.getServletPath());
            }
        }
        dispatch(forwardRequest,forwardResponse);
    }

    /**
     * 引入其他servlet的响应内容 (注:其他servlet可以写入数据,但无法提交数据)
     *
     *  前提 : 需要实现 Transfer-Encoding
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        NettyHttpServletResponse httpResponse = ServletUtil.unWrapper(response);
        if(httpResponse == null){
            throw new UnsupportedOperationException("Not found Original Response");
        }
        HttpServletRequest httpRequest = ServletUtil.unWrapper(request);
        if(httpRequest == null){
            throw new UnsupportedOperationException("Not found Original Request");
        }

        //切换至分块传输流
        httpResponse.changeToChunkStream();
        // ServletHttpIncludeResponse.class 会禁止操作数据
        NettyHttpServletIncludeResponse includeResponse = new NettyHttpServletIncludeResponse(httpResponse);
        // ServletHttpIncludeRequest.class 会将新数据传递下去
        NettyHttpServletIncludeRequest includeRequest = new NettyHttpServletIncludeRequest(httpRequest);

        //根据名称
        if (path == null) {
            includeRequest.setIncludeName(name);
            includeRequest.setPaths(httpRequest.getPathInfo(),httpRequest.getQueryString(),httpRequest.getRequestURI(),httpRequest.getServletPath());
            includeRequest.setParameterMap(httpRequest.getParameterMap());
        } else {
            includeRequest.setIncludePath(path);
            //根据路径
            if (includeRequest.getAttribute(INCLUDE_REQUEST_URI) == null) {
                includeRequest.setAttribute(INCLUDE_REQUEST_URI, includeRequest.getRequestURI());
                includeRequest.setAttribute(INCLUDE_CONTEXT_PATH, includeRequest.getContextPath());
                includeRequest.setAttribute(INCLUDE_PATH_INFO, includeRequest.getPathInfo());
                includeRequest.setAttribute(INCLUDE_QUERY_STRING, includeRequest.getQueryString());
                includeRequest.setAttribute(INCLUDE_SERVLET_PATH, includeRequest.getServletPath());
            }
        }
        dispatch(includeRequest,includeResponse);
    }

    /**
     * 调度
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void dispatch(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        try {
            if(request instanceof NettyHttpServletRequest){
                ((NettyHttpServletRequest) request).setAsyncSupportedFlag(filterChain.getServletRegistration().isAsyncSupported());
            }
            filterChain.doFilter(request, response);
        }finally {
            recycle();
        }
    }

    /**
     * 调度 (异步)
     * @param request
     * @param response
     * @param asyncContext
     * @return
     */
    public Runnable dispatchAsync(HttpServletRequest request, HttpServletResponse response, NettyServletAsyncContext asyncContext){
        if(path == null){
            return null;
        }
        if(request instanceof NettyHttpServletAsyncRequest
                && path.equals(request.getAttribute(AsyncContext.ASYNC_REQUEST_URI))){
            return null;
        }

        NettyHttpServletResponse httpResponse = ServletUtil.unWrapper(response);
        if(httpResponse == null){
            throw new UnsupportedOperationException("Not found Original Response");
        }
        HttpServletRequest httpRequest = ServletUtil.unWrapper(request);
        if(httpRequest == null){
            throw new UnsupportedOperationException("Not found Original Request");
        }
        if (response.isCommitted()) {
            throw new IllegalStateException("Cannot perform this operation after response has been committed");
        }

        //移交输出流的控制权
        NettyServletOutputStreamWrapper outWrapper;
        try {
            outWrapper = httpResponse.getOutputStream();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(),e);
        }

        //暂停当前响应
        outWrapper.setSuspendFlag(true);
        //交给下一个servlet
        NettyHttpServletAsyncResponse asyncResponse = new NettyHttpServletAsyncResponse(httpResponse,outWrapper.unwrap());
        NettyHttpServletAsyncRequest asyncRequest = new NettyHttpServletAsyncRequest(request,asyncContext);
        asyncRequest.setDispatchPath(path);
        if (asyncRequest.getAttribute(AsyncContext.ASYNC_REQUEST_URI) == null) {
            asyncRequest.setAttribute(AsyncContext.ASYNC_CONTEXT_PATH, asyncRequest.getContextPath());
            asyncRequest.setAttribute(AsyncContext.ASYNC_PATH_INFO, asyncRequest.getPathInfo());
            asyncRequest.setAttribute(AsyncContext.ASYNC_QUERY_STRING, asyncRequest.getQueryString());
            asyncRequest.setAttribute(AsyncContext.ASYNC_REQUEST_URI, asyncRequest.getRequestURI());
            asyncRequest.setAttribute(AsyncContext.ASYNC_SERVLET_PATH, asyncRequest.getServletPath());
        }

        //返回任务
        Runnable runnable = ()->{
            try {
                dispatch(asyncRequest, asyncResponse);
            } catch (Exception e) {
                throw new NettyServletAsyncContext.AsyncRuntimeException(e);
            }
        };
        return runnable;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if(filterChain == null){
            return null;
        }
        return filterChain.getServletRegistration().getName();
    }
   
    @Override
    public void recycle() {
        path = null;
        name = null;
        filterChain = null;
        RECYCLER.recycleInstance(this);
    }

}
