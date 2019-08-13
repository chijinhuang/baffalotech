package com.baffalotech.integration.tcp.handler;

import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPResponse;

import io.netty.channel.ChannelHandlerContext;

/**
 * 链式处理器
 * @author chijinhuang
 *
 */
public interface TCPHandler {

	public void handle(ChannelHandlerContext ctx,TCPRequest tcpRequest,TCPResponse tcpResponse,TCPHandlerChain chain);
	
	public String getName();
	
	public void setName(String name);
	
	public interface TCPRootHandler{
		
		public void hanle(TCPRequest tcpRequest,TCPResponse tcpResponse);
		
	}
}
