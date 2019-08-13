package com.baffalotech.integration.http.netty.servlet.handler;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.configuration.IntegrationServerProperties;
import com.baffalotech.integration.http.netty.core.AbstractChannelHandler;
import com.baffalotech.integration.http.netty.core.MessageToRunnable;
import com.baffalotech.integration.http.netty.servlet.NettyHttpServletSession;
import com.baffalotech.integration.http.netty.servlet.NettyServletContext;
import com.baffalotech.integration.http.netty.servlet.support.HttpServletObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * servlet处理器 (服务器的入口)
 * @author acer01
 *  2018/7/1/001
 */
@ChannelHandler.Sharable
public class NettyServletChannelHandler extends AbstractChannelHandler<Object> {
	
	private static final Logger logger = LoggerFactory.getLogger(NettyServletChannelHandler.class);

    private Executor dispatcherExecutor;
    private HttpMessageToServletRunnable httpMessageToServletRunnable;
    public static final AttributeKey<MessageToRunnable> CHANNEL_ATTR_KEY_MESSAGE_TO_RUNNABLE = AttributeKey.valueOf(MessageToRunnable.class + "#Handler-MessageToRunnable");

    public NettyServletChannelHandler(NettyServletContext servletContext, IntegrationServerProperties properties,Executor dispatcherExecutor) {
        super(false);
        this.httpMessageToServletRunnable = new HttpMessageToServletRunnable(servletContext,properties);
        this.dispatcherExecutor = dispatcherExecutor;
    }

    @Override
    protected void onMessageReceived(ChannelHandlerContext context, Object msg) throws Exception {
        MessageToRunnable messageToRunnable = getMessageToRunnable(context.channel());
        if(messageToRunnable == null){
            messageToRunnable = httpMessageToServletRunnable;
            setMessageToRunnable(context.channel(),messageToRunnable);
        }

        Runnable task = messageToRunnable.newRunnable(context,msg);
        if(dispatcherExecutor != null){
            dispatcherExecutor.execute(task);
        }else {
            task.run();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        saveAndClearSession(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("{}",cause);
        saveAndClearSession(ctx);
        ctx.channel().close();
    }

    /**
     * 保存并且清空会话
     * @param ctx
     */
    protected void saveAndClearSession(ChannelHandlerContext ctx){
        NettyHttpServletSession httpSession = HttpServletObject.getSession(ctx);
        if(httpSession != null) {
            if (httpSession.isValid()) {
                httpSession.save();
            } else if (httpSession.getId() != null) {
                httpSession.remove();
            }
            httpSession.clear();
        }
    }

    /**
     * 把IO任务包工厂类 放到这个连接上
     * @param channel
     * @param messageToRunnable
     */
    public static void setMessageToRunnable(Channel channel, MessageToRunnable messageToRunnable){
        channel.attr(CHANNEL_ATTR_KEY_MESSAGE_TO_RUNNABLE).set(messageToRunnable);
    }

    /**
     * 取出这个连接上的 IO任务包工厂类
     * @param channel
     * @return
     */
    public static MessageToRunnable getMessageToRunnable(Channel channel){
        MessageToRunnable taskFactory = channel.attr(CHANNEL_ATTR_KEY_MESSAGE_TO_RUNNABLE).get();
        return taskFactory;
    }

}
