package org.baffalotech.integration.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.annotation.Connector;
import com.baffalotech.integration.mvc.IContext;
import com.baffalotech.integration.mvc.IFilter;
import com.baffalotech.integration.mvc.IFilterChain;

@Connector(name = "default")
public class MVCTestFilter implements IFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MVCTestFilter.class);

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doFilter(IContext context, IFilterChain chain) {
		// TODO Auto-generated method stub
		LOGGER.info("before mvc");
		chain.doFilter(context);
		LOGGER.info("after mvc");
	}

}
