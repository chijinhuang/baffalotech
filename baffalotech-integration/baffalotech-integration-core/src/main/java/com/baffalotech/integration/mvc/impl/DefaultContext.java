package com.baffalotech.integration.mvc.impl;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.mvc.ContentType;
import com.baffalotech.integration.mvc.IContext;

public class DefaultContext implements IContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContext.class);
	
	private Object payload;
	
	private Object request;
	
	private String connectorName;
	
	private String encoding;
	
	private ContentType contentType;
	
	private DefaultContext()
	{
		
	}
	@Override
	public Object getPayload() {
		// TODO Auto-generated method stub
		return this.payload;
	}

	@Override
	public void setPayload(Object payload) {
		// TODO Auto-generated method stub
		this.payload = payload.toString();
	}

	@Override
	public Object getRequest() {
		// TODO Auto-generated method stub
		return request;
	}
	
	@Override
	public String getConnectorName() {
		// TODO Auto-generated method stub
		return this.connectorName;
	}

	@Override
	public ContentType getContentType() {
		// TODO Auto-generated method stub
		return contentType;
	}

	@Override
	public String getEncoding() {
		// TODO Auto-generated method stub
		return this.encoding;
	}

	@Override
	public String getPayloadString() {
		// TODO Auto-generated method stub
		if(payload instanceof byte[])
		{
			try {
				return new String((byte[])payload,encoding);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				LOGGER.warn("unsupport encoding:{}",encoding);
				return new String((byte[])payload);
			}
		}else if (payload instanceof String) {
			return (String)payload;
		}else {
			LOGGER.warn("payload is not instance of byte[] or String class,please check");
		}
		return null;
	}
	
	public static class Builder{
		private Object payload;
		
		private Object request;
		
		private String connectorName;
		//默认是UTF-8编码
		private String encoding = "UTF-8";
		
		private ContentType contentType = ContentType.XML;
		
		private Builder()
		{
		
		}
		
		public static Builder newBuilder()
		{
			return new Builder();
		}
		
		public Builder payload(Object payload) {
			this.payload = payload;
			return this;
		}
		
		public Builder request(Object request) {
			this.request = request;
			return this;
		}
		
		public Builder connectorName(String connectorName)
		{
			this.connectorName = connectorName;
			return this;
		}
		
		public Builder encoding(String encoding)
		{
			this.encoding = encoding;
			return this;
		}
		
		public Builder contentType(ContentType contentType)
		{
			this.contentType = contentType;
			return this;
		}
		
		public DefaultContext build()
		{
			DefaultContext context  = new DefaultContext();
			context.connectorName = connectorName;
			context.payload = payload;
			context.encoding = encoding;
			context.request = request;
			context.contentType = contentType;
			return context;
		}
	}
}
