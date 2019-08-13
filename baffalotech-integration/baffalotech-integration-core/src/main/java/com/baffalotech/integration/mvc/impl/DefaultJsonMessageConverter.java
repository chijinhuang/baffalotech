package com.baffalotech.integration.mvc.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.mvc.MessageConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DefaultJsonMessageConverter implements MessageConverter<Object> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJsonMessageConverter.class);

	@Override
	public Object decode(String payload, Class<Object> convertType) {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(payload, convertType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("parse json error");
			throw new IllegalStateException(e);
		} 
	}

	@Override
	public String encode(Object value) {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			LOGGER.error("convert to json error");
			throw new IllegalStateException(e);
		}
	}
}
