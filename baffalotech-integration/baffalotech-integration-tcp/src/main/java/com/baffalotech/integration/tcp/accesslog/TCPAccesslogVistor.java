package com.baffalotech.integration.tcp.accesslog;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import com.baffalotech.integration.accesslog.AbstractAccessLogVistorAdapter;
import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPResponse;

import io.netty.channel.ChannelHandlerContext;

public class TCPAccesslogVistor extends AbstractAccessLogVistorAdapter {

	private ChannelHandlerContext ctx;
	private TCPRequest request;
	private TCPResponse response;
	
	public TCPAccesslogVistor(ChannelHandlerContext ctx,TCPRequest request,TCPResponse response)
	{
		this.ctx = ctx;
		this.request = request;
		this.response = response;
	}
	
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		InetSocketAddress remote = (InetSocketAddress)ctx.channel().remoteAddress();
		return remote.getHostName();
	}
	
	public String getHost() {
		// TODO Auto-generated method stub
		InetSocketAddress local = (InetSocketAddress)ctx.channel().localAddress();
		return local.getHostName();
	}
	
	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return "tcp";
	}
	
	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 200;
	}
	
	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		InetSocketAddress local = (InetSocketAddress)ctx.channel().localAddress();
		return local.getPort();
	}
	
	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		InetSocketAddress remote = (InetSocketAddress)ctx.channel().localAddress();
		return remote.getPort();
	}
	
	@Override
	public int getBytesWritten() {
		// TODO Auto-generated method stub
		return response.getData() == null?0:response.getData().length;
	}
	
	@Override
	public long getCommitTime() {
		// TODO Auto-generated method stub
		return new Date().getTime();
	}
	
	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		InetSocketAddress local = (InetSocketAddress)ctx.channel().localAddress();
		return local.getHostName();
	}
	
	@Override
	public Enumeration<String> getHeaders(String headerName) {
		// TODO Auto-generated method stub
		Set<String> values = new HashSet<String>();
		if(request.getHeader(headerName) != null)
		{
			values.add(request.getHeader(headerName));
		}
		return Collections.enumeration(values);
	}
	
	@Override
	public Set<String> getResponseHeaders(String name) {
		// TODO Auto-generated method stub
		Set<String> values = new HashSet<String>();
		if(response.getAttribute(name) != null)
		{
			values.add(response.getHeader(name));
		}
		return values;
	}
	
	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return request.getAttribute(name);
	}
}
