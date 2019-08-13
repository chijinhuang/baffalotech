package com.baffalotech.integration.tcp.connector;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baffalotech.integration.tcp.LengthField;
import com.baffalotech.integration.tcp.TCPProtocal;
import com.baffalotech.integration.tcp.TCPRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TCPFixedLengthDecoder extends ByteToMessageDecoder  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPFixedLengthDecoder.class);
	
	private TCPProtocal inputProtocal;
	
	private boolean flag = false;
	
	public TCPFixedLengthDecoder(TCPProtocal inputProtocal)
	{ 
		this.inputProtocal = inputProtocal;
	}
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if(!flag)
		{
			int headerLength = inputProtocal.getHeaderLength();
			if(!in.isReadable(headerLength))
			{
				//wait
				return;
			}
			byte[] headerBytes = new byte[headerLength];
			in.readBytes(headerBytes);
			inputProtocal.parse(new String(headerBytes));
			flag = true;
		}else {
			int dataLength = inputProtocal.getDataLength();
			if(in.isReadable(dataLength))
			{
				byte[] data = new byte[dataLength];
				in.readBytes(data);
				TCPRequest tcpRequest = new TCPRequest();
				setTcpRequest(data,tcpRequest);
				out.add(tcpRequest);
				flag = false;
				//读取完毕
			}
		}
	}
	
	protected void setTcpRequest(byte[] data,TCPRequest request) {
		request.setData(data);
		inputProtocal.getFieldList().forEach(field -> {
			if(!(field instanceof LengthField))
			{
				request.setHeader(field.getName(), field.getValue());
			}
		});
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.error("error happen: remote ip is {},expection:{}",ctx.channel().remoteAddress(),cause);
		ctx.close();
	}
}
