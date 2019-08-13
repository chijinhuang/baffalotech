package com.baffalotech.integration.mvc.impl;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baffalotech.integration.mvc.ContentType;
import com.baffalotech.integration.mvc.MessageConverter;

public class DefaultXmlMessageConverter implements MessageConverter<Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultXmlMessageConverter.class);

	@Override
	public Object decode(String payload, Class<Object> convertType) {
		// TODO Auto-generated method stub
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(convertType);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			return jaxbUnmarshaller.unmarshal(new StringReader(payload));
		} catch (JAXBException e) {
			LOGGER.error("convert xml error:{}", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String encode(Object value) {
		// TODO Auto-generated method stub
		try {
			JAXBContext context = JAXBContext.newInstance(value.getClass());
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter stringWriter = new StringWriter();
			m.marshal(value, stringWriter);
			return stringWriter.toString();
		} catch (JAXBException e) {
			LOGGER.error("convert xml error:{}", e);
			throw new IllegalStateException(e);
		}
	}
}
