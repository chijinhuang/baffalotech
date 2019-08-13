package com.baffalotech.integration.http.netty.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class NettyProxyFilter implements Filter {
	
	private Filter proxyFilter;
	
	private String connectorName;
	
	private NettyProxyFilter(String connectorName,Filter proxyFilter)
	{
		this.connectorName = connectorName;
		this.proxyFilter = proxyFilter;
	}
	
	public static NettyProxyFilter createNettyProxyFilter(String connectorName,Filter proxyFilter)
	{
		return new NettyProxyFilter(connectorName, proxyFilter);
	}
	
	public String getConnectorName() {
		return connectorName;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		proxyFilter.doFilter(request, response, chain);
	}

}
