package com.baffalotech.integration.http.netty.springboot.server;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.baffalotech.integration.api.Connector;
import com.baffalotech.integration.api.Container;
import com.baffalotech.integration.http.netty.connector.NettyHttpServerConnector;
import com.baffalotech.integration.http.netty.connector.NettyHttpServerConnectorFactory;
import com.baffalotech.integration.http.netty.servlet.NettyServletContextInitializer;

public class NettyContainerServerFactory extends AbstractServletWebServerFactory implements ApplicationContextAware{
	
	@Autowired
	private Container container;
	
	@Autowired
	private NettyHttpServerConnectorFactory httpServerConnectorFactory;
	
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}

	@Override
	public WebServer getWebServer(ServletContextInitializer... initializers) {
		// TODO Auto-generated method stub
		NettyServerContainer nettyContainerServer = new NettyServerContainer(container);
		Connector defaultConnector = httpServerConnectorFactory.createNettyHttpServerConnector("default", getPort());
		container.addConnector(defaultConnector);
		Map<String, Connector> connectorMap = applicationContext.getBeansOfType(Connector.class);
		connectorMap.forEach((key,value)->{
			container.addConnector(value);
		});
		servletContextInitiale(container.getConnectorList(), initializers);
		return nettyContainerServer;
	}
	
	protected void servletContextInitiale(List<Connector> connectorList,ServletContextInitializer... initializers)
	{
		connectorList.forEach(connector -> {
			if(connector instanceof NettyHttpServerConnector)
			{
				for(ServletContextInitializer servletContextInitializer : initializers)
				{
					NettyHttpServerConnector httpServerConnector = (NettyHttpServerConnector)connector;

					try {
						servletContextInitializer.onStartup(httpServerConnector.getServletContext());
					} catch (ServletException e) {
						// TODO Auto-generated catch block
						throw new IllegalStateException(e.getMessage(),e);
					}
				}
			}
		});
	}
}
