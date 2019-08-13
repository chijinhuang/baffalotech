package com.baffalotech.integration.tcp.connector;

import java.util.Date;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.accesslog.AccessLogGenerator;
import com.baffalotech.integration.accesslog.AccessLogReceiver;
import com.baffalotech.integration.api.AbstractNettyConnector;
import com.baffalotech.integration.configuration.IntegrationServerProperties;
import com.baffalotech.integration.core.StandardThreadExecutor;
import com.baffalotech.integration.tcp.FixedLengthField;
import com.baffalotech.integration.tcp.LengthField;
import com.baffalotech.integration.tcp.TCPProtocal;
import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPRequestHanlder;
import com.baffalotech.integration.tcp.TCPResponse;
import com.baffalotech.integration.tcp.accesslog.TCPAccesslogVistor;
import com.baffalotech.integration.tcp.handler.TCPHandlerChain;
import com.baffalotech.integration.tcp.handler.TCPHandlerChainFactory;
import com.baffalotech.integration.util.ApplicationContextUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TCPFixedLengthHandler extends SimpleChannelInboundHandler<TCPRequest> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TCPFixedLengthHandler.class);

	private TCPRequestHanlder tcpRequestHanlder;
	private TCPProtocal outTcpProtocal;
	private AbstractNettyConnector nettyConnector;

	public TCPFixedLengthHandler(AbstractNettyConnector nettyConnector, TCPRequestHanlder tcpRequestHanlder,
			TCPProtocal outTcpProtocal) {
		this.tcpRequestHanlder = tcpRequestHanlder;
		this.outTcpProtocal = outTcpProtocal;
		this.nettyConnector = nettyConnector;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TCPRequest msg) throws Exception {
		// TODO Auto-generated method stub
		TCPResponse tcpResponse = new TCPResponse();
		if (outTcpProtocal == null) {
			this.outTcpProtocal = msg.getInTcpProtocal();
		}
		tcpResponse.setOutTcpProtocal(outTcpProtocal);
		// copy request attribute to response
		tcpResponse.getHeaderMap().putAll(msg.getHeaderMap());
		StandardThreadExecutor serverExecutor = nettyConnector.getContainer().getServerExecutor();
		try {
			serverExecutor.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					processRequest(ctx, msg, tcpResponse);
				}
			});
		} catch (RejectedExecutionException rejectException) {
			// TODO: handle exception
			LOGGER.warn(
					"process thread pool is full, reject, active={} poolSize={} corePoolSize={} maxPoolSize={} taskCount={}",
					serverExecutor.getActiveCount(), serverExecutor.getPoolSize(), serverExecutor.getCorePoolSize(),
					serverExecutor.getMaximumPoolSize(), serverExecutor.getTaskCount());
			ctx.close();
		}
	}

	protected void processRequest(ChannelHandlerContext ctx, TCPRequest tcpRequest, TCPResponse tcpResponse) {
		long beginTime = System.currentTimeMillis();
		try {
			TCPHandlerChainFactory factory = ApplicationContextUtil.getBean(TCPHandlerChainFactory.class);
			TCPHandlerChain chain = factory.create(nettyConnector.getName());
			chain.handle(ctx, tcpRequest, tcpResponse);
			// copy response attribute to out tcp protocal
			outTcpProtocal.getFieldList().forEach(field -> {
				if (field instanceof LengthField) {
					((LengthField) field).setValue(tcpResponse.getData().length);
				} else {
					String name = field.getName();
					String value = tcpResponse.getHeader(name);
					((FixedLengthField) field).setValue(value);
				}
			});
			// write reponse to channel
			ByteBuf out = ctx.alloc().buffer();
			// write header
			outTcpProtocal.getFieldList().forEach(field -> {
				out.writeBytes(field.toFieldText().getBytes());
			});
			// write data
			out.writeBytes(tcpResponse.getData());
			ctx.writeAndFlush(out);
			ctx.close();
		}catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("{}",e);
			ctx.close();
		} finally {
			// TODO: handle finally clause
			IntegrationServerProperties properties = ApplicationContextUtil.getBean(IntegrationServerProperties.class);
			if(properties.getAccesslog().isEnabled()) {
          	  AccessLogReceiver accessLogReceiver = ApplicationContextUtil.getBean(AccessLogReceiver.class);
                AccessLogGenerator accessLogGenerator = new AccessLogGenerator(properties.getAccesslog().getPattern());
                long finishTime = System.currentTimeMillis()-beginTime;
                String logMessage = accessLogGenerator.generateLog(new Date(), finishTime, new TCPAccesslogVistor(ctx,tcpRequest,tcpResponse));
                accessLogReceiver.logMessage(logMessage);
          }
		}
	}
}
