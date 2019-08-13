package com.baffalotech.integration.tcp.handler;

public abstract class AbstractTCPHandler implements TCPHandler {
	
	private String name;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

}
