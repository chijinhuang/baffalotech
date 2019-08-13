package com.baffalotech.integration.tcp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baffalotech.integration.tcp.connector.NettyTCPFixedLengthServerConnectorFactory;
import com.baffalotech.integration.tcp.handler.impl.SpringTCPHandlerChainFactory;

@Configuration
public class TCPServerConnectorAutoConfiguration {

	@Bean
	public NettyTCPFixedLengthServerConnectorFactory nettyTCPFixedLengthServerConnectorFactory()
	{
		return new NettyTCPFixedLengthServerConnectorFactory();
	}
	
	@Bean
	public SpringTCPHandlerChainFactory springTCPHandlerChainFactory()
	{
		return new SpringTCPHandlerChainFactory();
	}
}
