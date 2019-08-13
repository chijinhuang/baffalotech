package com.baffalotech.integration.http.netty.springboot.server;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

import com.baffalotech.integration.api.Container;

/**
 * 已服务容器作为server
 * @author chijinhuang
 *
 */
public class NettyServerContainer implements WebServer {

	private Container container;
	
	public NettyServerContainer(Container container)
	{
		this.container = container;
	}
	@Override
	public void start() throws WebServerException {
		// TODO Auto-generated method stub
		container.start();
	}

	@Override
	public void stop() throws WebServerException {
		// TODO Auto-generated method stub
		container.stop();
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
