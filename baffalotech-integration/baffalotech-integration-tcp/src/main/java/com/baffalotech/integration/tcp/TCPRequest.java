package com.baffalotech.integration.tcp;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装的TCP请求对象
 * @author chijinhuang
 *
 */
public class TCPRequest {

	private Map<String, Object> attributeMap = new HashMap<String, Object>();
	private Map<String, String> headerMap = new HashMap<String, String>();
	private TCPProtocal inTcpProtocal;
	private byte[] data;
	private String remoteAddress;
	private String remoteHost;
	private int remotePort;
	private String localName;
	private String localAddress;
	private int localPort;
	
	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}
	
	public void setAttributeMap(Map<String, Object> attributeMap) {
		this.attributeMap = attributeMap;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void setAttribute(String name,Object value)
	{
		this.attributeMap.put(name,value);
	}
	
	public String getHeader(String name)
	{
		return this.headerMap.get(name);
	}
	
	public Map<String, String> getHeaderMap() {
		return headerMap;
	}
	
	public void setHeader(String name,String value)
	{
		this.headerMap.put(name,value);
	}
	
	public void removeAttribute(String name)
	{
		this.attributeMap.remove(name);
	}
	
	public Object getAttribute(String name)
	{
		return this.getAttributeMap().get(name);
	}
	
	 public Enumeration<String> getAttributeNames(){
		 return Collections.enumeration(this.getAttributeMap().keySet());
	 }

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	public TCPProtocal getInTcpProtocal() {
		return inTcpProtocal;
	}
	
	public void setInTcpProtocal(TCPProtocal inTcpProtocal) {
		this.inTcpProtocal = inTcpProtocal;
	}
}
