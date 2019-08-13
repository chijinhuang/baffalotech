package com.baffalotech.integration.mvc;

public interface IFilter {
	
	int getOrder();

	void doFilter(IContext context,IFilterChain chain);
}
