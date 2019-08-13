package com.baffalotech.integration.mvc.impl;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baffalotech.integration.mvc.DiscriminatorValueReader;
import com.baffalotech.integration.mvc.IContext;

public class XmlDiscriminatorValueReader implements DiscriminatorValueReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlDiscriminatorValueReader.class);

	@Override
	public String parseDiscriminatorValue(IContext context, String path) {
		// TODO Auto-generated method stub
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = null;
			if(context.getPayload() instanceof byte[])
			{
				doc = builder.parse(new ByteArrayInputStream((byte[])context.getPayload()));
			}else if (context.getPayload() instanceof String) {
				String payload = (String)context.getPayload();
				doc = builder.parse(new ByteArrayInputStream(payload.getBytes(context.getEncoding())));
			}
			Element root = doc.getDocumentElement();
			Element currentNode = root;
			String[] paths = StringUtils.split(path, ".");
			for(String pathstring : paths)
			{
				NodeList childrenNodeList = currentNode.getChildNodes();
				boolean founded = false;
				for(int i = 0;i<childrenNodeList.getLength();i++)
				{
					Node node = childrenNodeList.item(i);
					if(node instanceof Element)
					{
						Element childElement = (Element)node;
						if(childElement.getNodeName().equals(pathstring))
						{
							currentNode = childElement;
							founded = true;
							break;
						}
					}
				}
				if(!founded)
				{
					throw new IllegalStateException("can not find discriminator value in payload");
				}
			}
			return currentNode.getTextContent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("parse xml node error");
			throw new IllegalStateException(e);
		}
	}

}
