package com.baffalotech.integration.http.netty.servlet.handler;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.integration.IntegrationProperties;

import com.baffalotech.integration.accesslog.AccessLogGenerator;
import com.baffalotech.integration.accesslog.AccessLogReceiver;
import com.baffalotech.integration.configuration.IntegrationServerProperties;
import com.baffalotech.integration.http.netty.accesslog.NettyHttpAccessLogVistor;
import com.baffalotech.integration.http.netty.core.MessageToRunnable;
import com.baffalotech.integration.http.netty.core.util.AbstractRecycler;
import com.baffalotech.integration.http.netty.core.util.ByteBufAllocatorX;
import com.baffalotech.integration.http.netty.core.util.Recyclable;
import com.baffalotech.integration.http.netty.servlet.NettyHttpServletRequest;
import com.baffalotech.integration.http.netty.servlet.NettyHttpServletResponse;
import com.baffalotech.integration.http.netty.servlet.NettyServletAsyncContext;
import com.baffalotech.integration.http.netty.servlet.NettyServletContext;
import com.baffalotech.integration.http.netty.servlet.NettyServletRequestDispatcher;
import com.baffalotech.integration.http.netty.servlet.support.HttpServletObject;
import com.baffalotech.integration.http.netty.servlet.support.NettyServletErrorPage;
import com.baffalotech.integration.http.netty.servlet.support.NettyServletErrorPageManager;
import com.baffalotech.integration.util.ApplicationContextUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * http任务
 * @author 84215
 */
public class HttpMessageToServletRunnable implements MessageToRunnable {

    public static final AtomicLong SERVLET_AND_FILTER_TIME = new AtomicLong();
    public static final AtomicLong SERVLET_QUERY_COUNT = new AtomicLong();
    private NettyServletContext servletContext;
    private IntegrationServerProperties config;

    private static final AbstractRecycler<HttpRunnable> RECYCLER = new AbstractRecycler<HttpRunnable>() {
        @Override
        protected HttpRunnable newInstance() {
            return new HttpRunnable();
        }
    };

    public HttpMessageToServletRunnable(NettyServletContext servletContext, IntegrationServerProperties config) {
        this.servletContext = servletContext;
        this.config = config;
    }

    @Override
    public Runnable newRunnable(ChannelHandlerContext context, Object msg) {
        if(!(msg instanceof FullHttpRequest)) {
            throw new IllegalStateException("不支持的类型");
        }

        HttpRunnable instance = RECYCLER.getInstance();
        instance.httpServletObject = HttpServletObject.newInstance(
                servletContext,
                config,
                ByteBufAllocatorX.forceDirectAllocator(context),
                (FullHttpRequest) msg);;
        instance.properties = config;

        if(instance.httpServletObject.isHttpKeepAlive()){
            //分段写入, 用于流传输, 防止响应数据过大
            ChannelPipeline pipeline = context.channel().pipeline();
            if(pipeline.get(ChunkedWriteHandler.class) == null) {
                ChannelHandlerContext httpContext = pipeline.context(HttpServerCodec.class);
                if(httpContext == null){
                    httpContext = pipeline.context(HttpRequestDecoder.class);
                }
                if(httpContext != null) {
                    pipeline.addAfter(
                            httpContext.name(), "ChunkedWrite",new ChunkedWriteHandler());
                }
            }
        }
        return instance;
    }

    /**
     * http任务
     */
    public static class HttpRunnable implements Runnable,Recyclable {
        private HttpServletObject httpServletObject;
        private IntegrationServerProperties properties;

        @Override
        public void run() {
            NettyHttpServletRequest httpServletRequest = httpServletObject.getHttpServletRequest();
            NettyHttpServletResponse httpServletResponse = httpServletObject.getHttpServletResponse();
            Throwable realThrowable = null;

            long beginTime = System.currentTimeMillis();
            try {
                NettyServletRequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(httpServletRequest.getRequestURI());
                if (dispatcher == null) {
                    httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                dispatcher.dispatch(httpServletRequest, httpServletResponse);

            }catch (Throwable throwable){
                realThrowable = throwable;
                if(throwable instanceof ServletException){
                    realThrowable = ((ServletException) throwable).getRootCause();
                }
            }finally {
                long totalTime = System.currentTimeMillis() - beginTime;
                SERVLET_AND_FILTER_TIME.addAndGet(totalTime);

                /*
                 * 错误页的获取依据有两种 1.依据异常类型 2.依据状态码
                 */
                if(realThrowable == null) {
                    realThrowable = (Throwable) httpServletRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                }
                NettyServletErrorPage errorPage = null;
                NettyServletErrorPageManager errorPageManager = httpServletObject.getServletContext().getErrorPageManager();
                if(realThrowable != null){
                    errorPage = errorPageManager.find(realThrowable);
                    if(errorPage == null) {
                        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        errorPage = errorPageManager.find(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                    if(errorPage == null) {
                        errorPage = errorPageManager.find(0);
                    }
                }else if(httpServletResponse.isError()) {
                    errorPage = errorPageManager.find(httpServletResponse.getStatus());
                    if(errorPage == null) {
                        errorPage = errorPageManager.find(0);
                    }
                }
                //处理错误页
                if(errorPage != null){
                    errorPageManager.handleErrorPage(errorPage,realThrowable,httpServletRequest,httpServletResponse);
                }
                if(properties.getAccesslog().isEnabled()) {
                	  AccessLogReceiver accessLogReceiver = ApplicationContextUtil.getBean(AccessLogReceiver.class);
                      AccessLogGenerator accessLogGenerator = new AccessLogGenerator(properties.getAccesslog().getPattern());
                      long finishTime = System.currentTimeMillis()-beginTime;
                      String logMessage = accessLogGenerator.generateLog(new Date(), finishTime, new NettyHttpAccessLogVistor(httpServletRequest, httpServletResponse));
                      accessLogReceiver.logMessage(logMessage);
                }
              

                /*
                 * 如果不是异步， 或者异步已经结束
                 * 每个响应对象是只有当在servlet的service方法的范围内或在filter的doFilter方法范围内是有效的，除非该
                 * 组件关联的请求对象已经开启异步处理。如果相关的请求已经启动异步处理，那么直到AsyncContext的
                 * complete 方法被调用，请求对象一直有效。为了避免响应对象创建的性能开销，容器通常回收响应对象。
                 * 在相关的请求的startAsync 还没有调用时，开发人员必须意识到保持到响应对象引用，超出之上描述的范
                 * 围可能导致不确定的行为
                 */
                if(httpServletRequest.isAsync()){
                    NettyServletAsyncContext asyncContext = httpServletRequest.getAsyncContext();
                    //如果异步执行完成, 进行回收
                    if(asyncContext.isComplete()){
                        httpServletObject.recycle();
                    }else {
                        //标记主线程已经执行结束
                        httpServletRequest.getAsyncContext().markIoThreadOverFlag();
                    }
                }else {
                    //不是异步直接回收
                    httpServletObject.recycle();
                }
                recycle();
                SERVLET_QUERY_COUNT.incrementAndGet();
            }
        }

        @Override
        public void recycle() {
            httpServletObject = null;
            RECYCLER.recycleInstance(HttpRunnable.this);
        }

    }

}
