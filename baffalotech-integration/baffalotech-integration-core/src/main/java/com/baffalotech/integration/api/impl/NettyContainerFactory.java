package com.baffalotech.integration.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.baffalotech.integration.api.Connector;
import com.baffalotech.integration.api.Container;
import com.baffalotech.integration.api.ContainerFactory;
import com.baffalotech.integration.configuration.IntegrationServerProperties;

public class NettyContainerFactory implements ContainerFactory,ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	
	@Autowired
	private IntegrationServerProperties serverProperties;

	@Override
	public Container createContainer() {
		// TODO Auto-generated method stub
		NettyContainer defaultContainer = new NettyContainer(serverProperties);
		getConnectorList().forEach(connector -> {
			defaultContainer.addConnector(connector);
		});
		return defaultContainer;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}
	
	private List<Connector> getConnectorList(){
		List<Connector> connectorList = new ArrayList<Connector>();
		Map<String, Connector> beansOfTypes = this.applicationContext.getBeansOfType(Connector.class);	
		beansOfTypes.forEach((name,connector)->{
			connectorList.add(connector);
		});
		return connectorList;
	}
}
