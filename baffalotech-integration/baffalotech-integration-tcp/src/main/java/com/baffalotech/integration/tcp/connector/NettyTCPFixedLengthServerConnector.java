package com.baffalotech.integration.tcp.connector;

import com.baffalotech.integration.api.AbstractNettyConnector;
import com.baffalotech.integration.api.Container;
import com.baffalotech.integration.tcp.TCPProtocal;
import com.baffalotech.integration.tcp.TCPRequestHanlder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyTCPFixedLengthServerConnector extends AbstractNettyConnector {

    private TCPProtocal inTcpProtocal;
    private TCPProtocal ouTcpProtocal;
    private TCPRequestHanlder tcpRequestHanlder;
    
    public NettyTCPFixedLengthServerConnector(Container container)
    {
    	super(container);
    }
	
	public TCPProtocal getInTcpProtocal() {
		return inTcpProtocal;
	}
	
	public void setInTcpProtocal(TCPProtocal inTcpProtocal) {
		this.inTcpProtocal = inTcpProtocal;
	}
	
	public TCPProtocal getOuTcpProtocal() {
		return ouTcpProtocal;
	}
	
	public void setOuTcpProtocal(TCPProtocal ouTcpProtocal) {
		this.ouTcpProtocal = ouTcpProtocal;
	}
	
	public TCPRequestHanlder getTcpRequestHanlder() {
		return tcpRequestHanlder;
	}
	
	public void setTcpRequestHanlder(TCPRequestHanlder tcpRequestHanlder) {
		this.tcpRequestHanlder = tcpRequestHanlder;
	}

	@Override
	protected ChannelInitializer<? extends Channel> newInitializerChannelHandler() {
		// TODO Auto-generated method stub
		return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast("decode",new TCPFixedLengthDecoder(inTcpProtocal));
                p.addLast(new TCPFixedLengthHandler(NettyTCPFixedLengthServerConnector.this,tcpRequestHanlder, ouTcpProtocal));
            }
        };
	}	
}
