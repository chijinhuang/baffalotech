package com.baffalotech.integration.api;

import java.util.List;

import com.baffalotech.integration.configuration.IntegrationServerProperties;
import com.baffalotech.integration.core.StandardThreadExecutor;

/**
 * 容器接口 ，包含连接器列表
 * @author chijinhuang
 *
 */
public interface Container extends LifeCycle{

	public void setName(String name);
	
	public String getName();
	
	//添加连接器
	public void addConnector(Connector connector);
	
	//删除连接器
	public void removeConnector(String name);
	
	//删除连接器
	public void removeConnector(Connector connector);
	
	//返回连接器列表
	public List<Connector> getConnectorList();
	
	//返回接口执行的线程池
	public StandardThreadExecutor getServerExecutor();
	
	//access log 线程池
	public StandardThreadExecutor getAccessLogExecutor();
	
	//获取参数配置
	public IntegrationServerProperties getServerProperties();
}
