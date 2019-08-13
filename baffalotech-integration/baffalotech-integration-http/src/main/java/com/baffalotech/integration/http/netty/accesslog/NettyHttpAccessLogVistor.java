package com.baffalotech.integration.http.netty.accesslog;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

import com.baffalotech.integration.accesslog.AbstractAccessLogVistorAdapter;

public class NettyHttpAccessLogVistor extends AbstractAccessLogVistorAdapter {

	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	
	public NettyHttpAccessLogVistor(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse)
	{
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;
	}
	
	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return httpServletRequest.getQueryString();
	}
	
	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return httpServletRequest.getRemoteAddr();
	}
	
	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return httpServletRequest.getAttribute(name);
	}
	
	@Override
	public int getBytesWritten() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public long getCommitTime() {
		// TODO Auto-generated method stub
		return super.getCommitTime();
	}
	
	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return httpServletRequest.getLocalAddr();
	}
	
	@Override
	public Enumeration<String> getHeaders(String headerName) {
		// TODO Auto-generated method stub
		return httpServletRequest.getHeaders(headerName);
	}
	
	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return httpServletRequest.getMethod();
	}
	
	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return httpServletRequest.getProtocol();
	}
	
	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return httpServletRequest.getRemotePort();
	}
	
	@Override
	public String getRequestURI() {
		// TODO Auto-generated method stub
		return httpServletRequest.getRequestURI();
	}
	
	@Override
	public Set<String> getResponseHeaders(String name) {
		// TODO Auto-generated method stub
		Set<String> result = new HashSet<String>(httpServletResponse.getHeaders(name));
		return result;
	}
	
	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return httpServletRequest.getLocalPort();
	}
	
	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return httpServletResponse.getStatus();
	}
}
