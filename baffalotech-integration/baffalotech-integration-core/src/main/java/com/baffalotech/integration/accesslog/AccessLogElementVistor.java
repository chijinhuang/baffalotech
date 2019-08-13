package com.baffalotech.integration.accesslog;

import java.util.Enumeration;
import java.util.Set;

public interface AccessLogElementVistor {

	public String getQueryString();
	
	public String getRemoteAddr();
	
	public String getHost();
	
	public String getProtocol();
	
	public String getRemoteUser();
	
	public String getMethod();
	
	public String getRequestURI();
	
	public int getStatus();
	
	public Object getAttribute();
	
	public int getServerPort();
	
	public int getRemotePort();
	
	public int getBytesWritten();
	
	public long getCommitTime();
	
	public long getStartTime();
	
	public String getSessionId();
	
	public String getServerName();
	
	public Enumeration<String> getHeaders(String headerName);
	
	public Set<String> getResponseHeaders(String name);
	
	public Object getAttribute(String name);
}
