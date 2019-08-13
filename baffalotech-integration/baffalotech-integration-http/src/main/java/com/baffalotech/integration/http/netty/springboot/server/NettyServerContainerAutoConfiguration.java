package com.baffalotech.integration.http.netty.springboot.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baffalotech.integration.http.netty.connector.NettyHttpServerConnectorFactory;

@Configuration
public class NettyServerContainerAutoConfiguration {

	@Bean
	public NettyContainerServerFactory nettyContainerServerFactory()
	{
		return new NettyContainerServerFactory();
	}
	
	@Bean
	public NettyHttpServerConnectorFactory nettyHttpServerConnectorFactory()
	{
		return new NettyHttpServerConnectorFactory();
	}
}
