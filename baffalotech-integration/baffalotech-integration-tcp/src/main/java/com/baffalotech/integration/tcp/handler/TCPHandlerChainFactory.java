package com.baffalotech.integration.tcp.handler;

public interface TCPHandlerChainFactory {

	/**
	 * 根据connector name获取执行器链路
	 * @param name
	 * @return
	 */
	public TCPHandlerChain create(String name);
}
