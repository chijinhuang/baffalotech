package com.baffalotech.integration.configuration;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baffalotech.integration.accesslog.AccessLogReceiver;
import com.baffalotech.integration.accesslog.DefaultAccessLogReceiver;
import com.baffalotech.integration.api.Container;
import com.baffalotech.integration.api.ContainerFactory;
import com.baffalotech.integration.api.impl.NettyContainerFactory;
import com.baffalotech.integration.mvc.MessageConverter;
import com.baffalotech.integration.mvc.impl.DefaultJsonMessageConverter;
import com.baffalotech.integration.mvc.impl.DefaultXmlMessageConverter;
import com.baffalotech.integration.mvc.impl.RequestMappingHandlerMapping;
import com.baffalotech.integration.util.ApplicationContextUtil;

@Configuration
public class IntegrationServerAutoConfiguration {
	
	@Autowired
	private IntegrationServerProperties properties;

	@Bean
	public ContainerFactory containerFactory() {
		NettyContainerFactory containerFactory = new NettyContainerFactory();
		return containerFactory;
	}
	
	@Bean
	public Container container(ContainerFactory containerFactory)
	{
		return containerFactory.createContainer();
	}

	@Bean("integrationProperties")
	@ConditionalOnMissingBean(IntegrationServerProperties.class)
	public IntegrationServerProperties integrationProperties() {
		return new IntegrationServerProperties();
	}
	
	@Bean
	public AccessLogReceiver defaultAccessLogReceiver(Container container)
	{
		File dir = new File(properties.getAccesslog().getDirectory());
		if(!dir.exists())
		{
			dir.mkdir();
		}
		DefaultAccessLogReceiver defaultAccessLogReceiver = new DefaultAccessLogReceiver(container.getAccessLogExecutor(), dir,properties.getAccesslog().getPrefix(),properties.getAccesslog().getSuffix(),true);
		return defaultAccessLogReceiver;
	}
	
	@Bean
	public ApplicationContextUtil applicationContextUtil()
	{
		return new ApplicationContextUtil();
	}
	
	@Bean(name = "mvcRequestMappingHandlerMappingBean")
	public RequestMappingHandlerMapping requestMappingHandlerMapping()
	{
		return new RequestMappingHandlerMapping();
	}
	
	@Bean
	public MessageConverter defaultXmlMessageConverter()
	{
		return new DefaultXmlMessageConverter();
	}
	
	@Bean
	public MessageConverter defaultJsonMessageConverter()
	{
		return new DefaultJsonMessageConverter();
	}
}
