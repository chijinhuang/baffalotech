package com.baffalotech.integration.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.api.AbstractContainer;
import com.baffalotech.integration.configuration.IntegrationServerProperties;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 容器的默认实现
 * 
 * @author chijinhuang
 *
 */
public class NettyContainer extends AbstractContainer{

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyContainer.class);

	private EventLoopGroup boss = null;

	private EventLoopGroup worker = null;

	private boolean enableEpoll;
	private int ioThreadCount = 0;
	private int ioRatio = 100;

	public NettyContainer(IntegrationServerProperties serverProperties) {
		super(serverProperties.getContainerName(), serverProperties);
		init();
	}

	protected void setIoRatio(int ioRatio) {
		if (worker instanceof NioEventLoopGroup) {
			((NioEventLoopGroup) worker).setIoRatio(ioRatio);
			this.ioRatio = ioRatio;
		} else if (worker instanceof EpollEventLoopGroup) {
			((EpollEventLoopGroup) worker).setIoRatio(ioRatio);
			this.ioRatio = ioRatio;
		}
	}
	
	public void setIoThreadCount(int ioThreadCount) {
        this.ioThreadCount = ioThreadCount;
    }

	protected EventLoopGroup newWorkerEventLoopGroup() {
        EventLoopGroup worker;
        if(enableEpoll){
            worker = new EpollEventLoopGroup(ioThreadCount);
        }else {
            worker = new NioEventLoopGroup(ioThreadCount);
        }
        return worker;
    }

    protected EventLoopGroup newBossEventLoopGroup() {
        EventLoopGroup boss;
        if(enableEpoll){
            EpollEventLoopGroup epollBoss = new EpollEventLoopGroup(1);
            epollBoss.setIoRatio(ioRatio);
            boss = epollBoss;
        }else {
            NioEventLoopGroup jdkBoss = new NioEventLoopGroup(1);
            jdkBoss.setIoRatio(ioRatio);
            boss = jdkBoss;
        }
        return boss;
    }
    
    public EventLoopGroup getBoss() {
		return boss;
	}
    
    
    public EventLoopGroup getWorker() {
		return worker;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		this.enableEpoll = Epoll.isAvailable();
		this.ioThreadCount = getServerProperties().getServerIoThreads();
		this.ioRatio = getServerProperties().getServerIoRatio();
		this.boss = newBossEventLoopGroup();
		this.worker = newWorkerEventLoopGroup();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		boss.shutdownGracefully();
		worker.shutdownGracefully();
		LOGGER.info("container {} has been stopped",getName());
	}
}
