package com.baffalotech.integration.tcp.handler;

import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPResponse;

import io.netty.channel.ChannelHandlerContext;

public interface TCPHandlerChain {

	public void handle(ChannelHandlerContext ctx,TCPRequest tcpRequest,TCPResponse tcpResponse);
}
