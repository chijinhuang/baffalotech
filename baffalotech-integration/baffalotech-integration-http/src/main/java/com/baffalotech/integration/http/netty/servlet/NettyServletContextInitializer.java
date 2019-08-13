package com.baffalotech.integration.http.netty.servlet;

import org.springframework.boot.web.servlet.ServletContextInitializer;

public interface NettyServletContextInitializer extends ServletContextInitializer {

	/**
	 * 获取链接器名称
	 * @return
	 */
	public String getNettyName();
}
