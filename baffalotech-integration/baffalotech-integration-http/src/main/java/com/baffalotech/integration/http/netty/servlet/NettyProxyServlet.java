package com.baffalotech.integration.http.netty.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class NettyProxyServlet implements Servlet {
	
	private Servlet proxyServlet;
	
	private String connectorName;
	
	private NettyProxyServlet(String connectorName,Servlet proxyServlet) {
		// TODO Auto-generated constructor stub
		this.connectorName = connectorName;
		this.proxyServlet = proxyServlet;
	}
	
	public String getConnectorName() {
		return connectorName;
	}
	
	public static NettyProxyServlet createNettyProxyServlet(String connectorName,Servlet proxyServlet)
	{
		return new NettyProxyServlet(connectorName, proxyServlet);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		proxyServlet.init(config);
	}

	@Override
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return proxyServlet.getServletConfig();
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		proxyServlet.service(req, res);
	}

	@Override
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return proxyServlet.getServletInfo();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		proxyServlet.destroy();
	}

}
