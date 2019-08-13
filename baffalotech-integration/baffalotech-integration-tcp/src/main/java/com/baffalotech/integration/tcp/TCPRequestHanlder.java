package com.baffalotech.integration.tcp;

/**
 * TCP请求处理器
 * @author chijinhuang
 *
 */
public interface TCPRequestHanlder {

	/**
	 * 处理请求
	 * @param tcpRequest,请求
	 * @param tcpResponse 处理结果
	 */
	public void handle(TCPRequest tcpRequest,TCPResponse tcpResponse);
}
