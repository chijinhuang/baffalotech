package com.baffalotech.integration.api;

/**
 * 连接器接口
 * @author chijinhuang
 *
 */
public interface Connector extends LifeCycle {

	//设置连接器的名字
	public void setName(String name);
	
	//连接器的名字
	public String getName();
	
	//获取容器
	public Container getContainer();
	
	//设置容器
	public void setContainer(Container container);
}
