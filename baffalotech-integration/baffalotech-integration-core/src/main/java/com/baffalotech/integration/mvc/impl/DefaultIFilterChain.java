package com.baffalotech.integration.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.mvc.ContentType;
import com.baffalotech.integration.mvc.DiscriminatorValueReader;
import com.baffalotech.integration.mvc.HandlerMethod;
import com.baffalotech.integration.mvc.HandlerMethodMapping;
import com.baffalotech.integration.mvc.IContext;
import com.baffalotech.integration.mvc.IFilter;
import com.baffalotech.integration.mvc.IFilterChain;
import com.baffalotech.integration.mvc.MessageConverter;
import com.baffalotech.integration.mvc.annotation.DiscriminatorValue;

public class DefaultIFilterChain implements IFilterChain {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIFilterChain.class);
	
	private MessageConverter messageConverter;
	
	private List<IFilter> filterList = new ArrayList<IFilter>();
	
	private HandlerMethodMapping handlerMethodMapping;
	
	private int i = 0;
	
	public void setFilterList(List<IFilter> filterList) {
		this.filterList = filterList;
	}
	
	public void addFilter(IFilter filter)
	{
		this.filterList.add(filter);
	}
	
	public List<IFilter> getFilterList() {
		return filterList;
	}
	
	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
	
	public void setHandlerMethodMapping(HandlerMethodMapping handlerMethodMapping) {
		this.handlerMethodMapping = handlerMethodMapping;
	}

	@Override
	public void doFilter(IContext context) {
		// TODO Auto-generated method stub
		if(i<filterList.size())
		{
			filterList.get(i++).doFilter(context, this);
			return;
		}else if (i++==filterList.size()) {
			//convert payload to object
			String payload = context.getPayloadString();
			
			DiscriminatorValueReader discriminatorValueReader = null;
			if(context.getContentType() == ContentType.XML)
			{
				discriminatorValueReader = new XmlDiscriminatorValueReader();
			}else if (context.getContentType() == ContentType.JSON) {
				discriminatorValueReader = new JsonDiscriminatorValueReader();
			}
			
			String discriminatorValue = discriminatorValueReader.parseDiscriminatorValue(context, handlerMethodMapping.getDiscriminator(context.getConnectorName()));
			
			HandlerMethod handlerMethod = handlerMethodMapping.getHandlerMethod(context.getConnectorName(), discriminatorValue);
			
			Class parameterType = handlerMethod.getMethodParameters()[0].getParameterType();
			Class returnType = handlerMethod.getReturnType().getParameterType();
			Object args = messageConverter.decode(payload, parameterType);
			
			
			try {
				Object returnValue = handlerMethod.getBridgedMethod().invoke(handlerMethod.getBean(), args);
				context.setPayload(messageConverter.encode(returnValue));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("invoke error");
				throw new IllegalStateException(e);
			}
		}
	}
}
