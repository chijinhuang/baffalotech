package com.baffalotech.integration.api;

/*
 * 容器的的生命周期管理
 */
public interface LifeCycle {

	/**
	 * 在加载的时候启动
	 */
	public void start();
	
	/**
	 * 关闭各种资源，例如线程池，输入输出流，清空对象
	 */
	public void stop();
	
	/**
	 * 获取当前状态
	 * @return
	 */
	public LifeStatus getStatus();
}
