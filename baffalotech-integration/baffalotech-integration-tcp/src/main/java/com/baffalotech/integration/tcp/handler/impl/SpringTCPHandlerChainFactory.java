package com.baffalotech.integration.tcp.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.baffalotech.integration.annotation.Connector;
import com.baffalotech.integration.tcp.handler.TCPHandler;
import com.baffalotech.integration.tcp.handler.TCPHandlerChain;
import com.baffalotech.integration.tcp.handler.TCPHandlerChainFactory;
import com.baffalotech.integration.tcp.handler.TCPHandler.TCPRootHandler;

public class SpringTCPHandlerChainFactory implements TCPHandlerChainFactory,ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}

	@Override
	public TCPHandlerChain create(String name) {
		// TODO Auto-generated method stub
		Map<String, TCPHandler> handlersMap = applicationContext.getBeansOfType(TCPHandler.class);
		List<TCPHandler> handlers = new ArrayList<TCPHandler>();
		handlersMap.forEach((key,value)->{
			if(StringUtils.equals("*", value.getName()) || StringUtils.equals(value.getName(), name))
			{
				handlers.add(value);
			}
		});
		Collections.sort(handlers, new TCPHandlerComparator());
		
		Map<String,TCPHandler.TCPRootHandler> rootHandlerMap = applicationContext.getBeansOfType(TCPRootHandler.class);
		TCPRootHandler rootHandler = null;
		for(TCPRootHandler tcpRootHandler : rootHandlerMap.values())
		{
			Connector connector = tcpRootHandler.getClass().getAnnotation(Connector.class);
			if(StringUtils.equals("*", connector.name()) || StringUtils.equals(connector.name(), name))
			{
				rootHandler = tcpRootHandler;
				break;
			}
		}
		return new DefaultTCPHandlerChain(handlers, rootHandler);
	}

	private static class TCPHandlerComparator implements Comparator<TCPHandler>{

		@Override
		public int compare(TCPHandler o1, TCPHandler o2) {
			// TODO Auto-generated method stub
			Connector c1 = o1.getClass().getAnnotation(Connector.class);
			Connector c2 = o2.getClass().getAnnotation(Connector.class);
			return c1.order() - c2.order();
		}
		
	}
}
