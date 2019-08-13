package com.baffalotech.integration.mvc;

public interface HandlerMethodMapping {
	
	HandlerMethod getHandlerMethod(String name,String value);
	
	String getDiscriminator(String name);

	HandlerMethod getHandlerMethod(String name, Object value);

	IFilterChain getFilterChain(IContext context);
}
