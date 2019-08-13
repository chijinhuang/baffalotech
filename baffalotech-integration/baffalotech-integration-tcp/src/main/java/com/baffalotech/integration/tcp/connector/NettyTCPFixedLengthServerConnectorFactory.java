package com.baffalotech.integration.tcp.connector;

import org.springframework.beans.factory.annotation.Autowired;

import com.baffalotech.integration.api.Container;
import com.baffalotech.integration.core.ServerConstanst;
import com.baffalotech.integration.tcp.TCPProtocal;
import com.baffalotech.integration.tcp.TCPRequestHanlder;

public class NettyTCPFixedLengthServerConnectorFactory {

	@Autowired
	private Container container;
	
	public NettyTCPFixedLengthServerConnector create(String name,int port,TCPProtocal inTcpProtocal,TCPProtocal ouTcpProtocal,TCPRequestHanlder requestHanlder)
	{
		NettyTCPFixedLengthServerConnector serverConnector = new NettyTCPFixedLengthServerConnector(container);
		serverConnector.setInTcpProtocal(inTcpProtocal);
		serverConnector.setOuTcpProtocal(ouTcpProtocal);
		serverConnector.setContainer(container);
		serverConnector.setName(name);
		serverConnector.setSchema(ServerConstanst.TCP_SCHEMA_TYPE);
		serverConnector.setPort(port);
		serverConnector.setTcpRequestHanlder(requestHanlder);
		return serverConnector;
	}
}
