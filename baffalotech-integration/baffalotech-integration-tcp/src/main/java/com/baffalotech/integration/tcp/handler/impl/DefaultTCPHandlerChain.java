package com.baffalotech.integration.tcp.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPResponse;
import com.baffalotech.integration.tcp.handler.TCPHandler;
import com.baffalotech.integration.tcp.handler.TCPHandlerChain;

import io.netty.channel.ChannelHandlerContext;

public class DefaultTCPHandlerChain implements TCPHandlerChain {
	
	private List<TCPHandler> tcpHandlerList = new ArrayList<TCPHandler>();
	
	private TCPHandler.TCPRootHandler rootHandler = null;
	
	private int i = 0;
	
	public DefaultTCPHandlerChain(List<TCPHandler> tcpHandlerList,TCPHandler.TCPRootHandler rootHandler)
	{
		this.tcpHandlerList = tcpHandlerList;
		this.rootHandler = rootHandler;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, TCPRequest tcpRequest, TCPResponse tcpResponse) {
		// TODO Auto-generated method stub
		if(i<tcpHandlerList.size())
		{
			tcpHandlerList.get(i++).handle(ctx, tcpRequest, tcpResponse, this);
		}
		if(i++ == tcpHandlerList.size() && rootHandler != null)
		{
			rootHandler.hanle(tcpRequest, tcpResponse);
		}
	}
}
