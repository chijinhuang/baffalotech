package com.baffalotech.integration.api;

public abstract class AbstractConnector extends AbstractLifeCycle implements Connector {

	// 端口
	private int port;

	// 协议类型
	private String schema;

	//名字
	private String name;
	
	//容器
	private Container container;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Container getContainer() {
		return container;
	}
	
	public void setContainer(Container container) {
		this.container = container;
	}
}
