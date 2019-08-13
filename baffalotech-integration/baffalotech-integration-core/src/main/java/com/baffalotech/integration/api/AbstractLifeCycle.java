package com.baffalotech.integration.api;

/**
 * 抽象生命周期管理
 * @author chijinhuang
 *
 */
public abstract class AbstractLifeCycle implements LifeCycle {
	
	private LifeStatus status = LifeStatus.INIT;

	@Override
	public void start() {
		// TODO Auto-generated method stub
		if(status != LifeStatus.INIT)
		{
			return;
		}
		
		status = LifeStatus.STARTING;
		doStart();
		status = LifeStatus.STARTED;
	}
	
	//提供子类实现
	public abstract void doStart();

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if(status != LifeStatus.STARTING || status != LifeStatus.STARTED)
		{
			return;
		}
		
		status = LifeStatus.STOPPING;
		doStop();
		status = LifeStatus.STOPPED;
	}
	
	//提供子类实现
	public abstract void doStop();

	@Override
	public LifeStatus getStatus() {
		// TODO Auto-generated method stub
		return status;
	}

}
