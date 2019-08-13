package com.baffalotech.integration.api;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.api.impl.NettyContainer;
import com.baffalotech.integration.core.StandardThreadExecutor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class AbstractNettyConnector extends AbstractConnector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNettyConnector.class);
	
	private ServerBootstrap bootstrap;
	
	private EventLoopGroup bossEventLoopGroup;
	
	private EventLoopGroup workerEventLoopGroup;
	
	private StandardThreadExecutor serverExecutor;
	
	private ChannelFuture channelFuture ;
	
	//是否使用公共IO线程池
	private boolean isPublicIOThreadPool = false;
	
	public AbstractNettyConnector(Container container)
	{
		setContainer(container);
		this.serverExecutor = container.getServerExecutor();
		bootstrap = new ServerBootstrap();
		if(container instanceof NettyContainer)
		{
			NettyContainer nettyContainer = (NettyContainer)container;
			this.bossEventLoopGroup = nettyContainer.getBoss();
			this.workerEventLoopGroup = nettyContainer.getWorker();
			this.isPublicIOThreadPool = true;
		}else {
			//非netty容器，自行创建
			this.bossEventLoopGroup = new NioEventLoopGroup();
			this.workerEventLoopGroup = new NioEventLoopGroup();
		}
	}
	
	protected abstract ChannelInitializer<?extends Channel> newInitializerChannelHandler();
	
	@Override
	public void doStart() {
		// TODO Auto-generated method stub
		bootstrap.group(this.bossEventLoopGroup, this.workerEventLoopGroup)
				 .channel(NioServerSocketChannel.class)
				  //允许在同一端口上启动同一服务器的多个实例，只要每个实例捆绑一个不同的本地IP地址即可
                 .option(ChannelOption.SO_REUSEADDR, true)
                 //用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
//                 .option(ChannelOption.SO_BACKLOG, 1024) // determining the number of connections queued

                 //禁用Nagle算法，即数据包立即发送出去 (在TCP_NODELAY模式下，假设有3个小包要发送，第一个小包发出后，接下来的小包需要等待之前的小包被ack，在这期间小包会合并，直到接收到之前包的ack后才会发生)
                 .childOption(ChannelOption.TCP_NODELAY, true)
                 //开启TCP/IP协议实现的心跳机制
                 .childOption(ChannelOption.SO_KEEPALIVE, true)
                 .childHandler(newInitializerChannelHandler());
		try {
			InetSocketAddress serverAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(),getPort());
			channelFuture = bootstrap.bind(getPort());
//			channelFuture = bootstrap.bind(serverAddress).sync();
			LOGGER.info("connector {} started at port {},protocal is {}",getName(),getPort(),getSchema());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("error happen when connector {} starting",getName());
		}
	}
	
	@Override
	public void doStop() {
		// TODO Auto-generated method stub
		//只有使用自己创建的线程池才需要关闭，否则在container中关闭
		try {
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error happen when connector {} close",getName());
		}
		if(!isPublicIOThreadPool)
		{
			this.bossEventLoopGroup.shutdownGracefully();
			this.workerEventLoopGroup.shutdownGracefully();
		}
	}
	
	protected StandardThreadExecutor getServerExecutor() {
		return serverExecutor;
	}
}
