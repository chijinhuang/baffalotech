package com.baffalotech.integration.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.configuration.IntegrationServerProperties;
import com.baffalotech.integration.core.DefaultThreadFactory;
import com.baffalotech.integration.core.StandardThreadExecutor;

public abstract class AbstractContainer extends AbstractLifeCycle implements Container {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractContainer.class);

	private StandardThreadExecutor serverExecutor;
	
	private StandardThreadExecutor accessLogExecutor;
	
	private List<Connector> connectorList = new ArrayList<Connector>();

	private String name;
	
	private IntegrationServerProperties serverProperties;
	
	public  AbstractContainer(String name,IntegrationServerProperties serverProperties)
	{
		this.name = name;
		this.serverProperties = serverProperties;
		this.serverExecutor = new StandardThreadExecutor(serverProperties.getMinWorkerThread(), 
														serverProperties.getMaxWorkerThread(), 
														serverProperties.getWorkerQueueSize(), 
														new DefaultThreadFactory("Netty-server-"+name,true));
		this.serverExecutor.prestartAllCoreThreads();
		
		this.accessLogExecutor = new StandardThreadExecutor(2,2,2, 
				new DefaultThreadFactory("access-log-"+name,true));
	}
	
	public StandardThreadExecutor getServerExecutor() {
		return serverExecutor;
	}
	
	public void setServerExecutor(StandardThreadExecutor serverExecutor) {
		this.serverExecutor = serverExecutor;
	}
	
	public IntegrationServerProperties getServerProperties() {
		return serverProperties;
	}
	
	public void setServerProperties(IntegrationServerProperties serverProperties) {
		this.serverProperties = serverProperties;
	}
	
	@Override
	public void doStart() {
		// 启动所有的connector
		connectorList.forEach(connector -> {
			connector.start();
			LOGGER.info("{} connector has bean started", connector.getName());
		});
		LOGGER.info("container {} has been stated", getName());
	}
	
	//初始化
	public abstract void init();

	@Override
	public void doStop() {
		// 关闭所有的connector
		connectorList.forEach(connector -> {
			connector.stop();
			LOGGER.info("{} connector has bean stopped", connector.getName());
		});
		this.serverExecutor.shutdownNow();
		//子类做一些关闭的事情
		destroy();
	}
	
	//销毁资源
	public abstract void destroy();

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void addConnector(Connector connector) {
		// TODO Auto-generated method stub
		connectorList.add(connector);
	}

	@Override
	public void removeConnector(String name) {
		// TODO Auto-generated method stub
		connectorList.removeIf(connector -> connector.getName().equals(name));
	}

	@Override
	public void removeConnector(Connector connector) {
		// TODO Auto-generated method stub
		connectorList.remove(connector);
	}
	
	@Override
	public List<Connector> getConnectorList() {
		// TODO Auto-generated method stub
		return this.connectorList;
	}
	
	public StandardThreadExecutor getAccessLogExecutor() {
		return accessLogExecutor;
	}
}
