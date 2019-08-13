package com.baffalotech.integration.http.netty.connector;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.api.AbstractNettyConnector;
import com.baffalotech.integration.api.Container;
import com.baffalotech.integration.http.netty.core.AbstractChannelHandler;
import com.baffalotech.integration.http.netty.core.ProtocolsRegister;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyHttpServerConnector extends AbstractNettyConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServerConnector.class);

	/**
	 * 协议注册器列表
	 */
	private List<ProtocolsRegister> protocolsRegisterList = new LinkedList<>();
	
	private ServletContext servletContext;

	public NettyHttpServerConnector(Container container) {
		super(container);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ChannelInitializer<? extends Channel> newInitializerChannelHandler() {
		return new ChannelInitializer<SocketChannel>() {
			ChannelHandler dynamicProtocolHandler = new DynamicProtocolChannelHandler();

			@ChannelHandler.Sharable
			class DynamicProtocolChannelHandler extends AbstractChannelHandler<ByteBuf> {
				private DynamicProtocolChannelHandler() {
					super(false);
				}

				@Override
				protected void onMessageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
					Channel channel = ctx.channel();
					channel.pipeline().remove(this);
					for (ProtocolsRegister protocolsRegister : protocolsRegisterList) {
						if (protocolsRegister.canSupport(msg)) {
							protocolsRegister.register(channel);
							channel.pipeline().fireChannelRead(msg);
							return;
						}
					}
					LOGGER.info("Received no support protocols. message=[{}]", msg.toString(Charset.forName("UTF-8")));
				}
			}

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				// HTTP编码解码
				pipeline.addLast("DynamicProtocolHandler", dynamicProtocolHandler);
			}
		};
	}

	/**
	 * 添加协议注册器
	 * 
	 * @param protocolsRegister
	 */
	public void addProtocolsRegister(ProtocolsRegister protocolsRegister) {
		protocolsRegisterList.add(protocolsRegister);
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@Override
	public void doStart() {
		try {
			for (ProtocolsRegister protocolsRegister : protocolsRegisterList) {
				protocolsRegister.onServerStart();
			} 
		} catch (Exception e) {
			// TODO: handle exception
			throw new IllegalStateException(e.getMessage(), e);
		}
		super.doStart();
	}
	
	@Override
	public void doStop() {
		// TODO Auto-generated method stub
		 try{
	            for(ProtocolsRegister protocolsRegister : protocolsRegisterList){
	                protocolsRegister.onServerStop();
	            }
	        } catch (Exception e) {
	            throw new IllegalStateException(e.getMessage(),e);
	        }
		super.doStop();
	}

}
