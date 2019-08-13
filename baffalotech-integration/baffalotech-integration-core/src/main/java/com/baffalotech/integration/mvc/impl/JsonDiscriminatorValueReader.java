package com.baffalotech.integration.mvc.impl;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.mvc.DiscriminatorValueReader;
import com.baffalotech.integration.mvc.IContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDiscriminatorValueReader implements DiscriminatorValueReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonDiscriminatorValueReader.class);

	@Override
	public String parseDiscriminatorValue(IContext context, String path) {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(context.getPayloadString());
			String[] paths = StringUtils.split(path,".");
			JsonNode currentNode  = root;
			for(String pathString : paths)
			{
				JsonNode node = currentNode.get(pathString);
				if(node != null)
				{
					currentNode = node;
				}else {
					throw new IllegalStateException("can not find discriminator value in payload");
				}
			}
			return currentNode.asText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("parse xml node error");
			throw new IllegalStateException(e);
		}
	}

}
