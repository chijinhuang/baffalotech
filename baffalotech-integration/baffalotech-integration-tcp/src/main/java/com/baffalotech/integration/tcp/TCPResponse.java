package com.baffalotech.integration.tcp;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class TCPResponse {

	private Map<String, Object> attributeMap = new HashMap<String, Object>();
	
	private Map<String, String> headerMap = new HashMap<String, String>();
	
	private byte[] data;
	private TCPProtocal outTcpProtocal;
	
	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}
	
	public void setAttributeMap(Map<String, Object> attributeMap) {
		this.attributeMap = attributeMap;
	}
	
	public Map<String, String> getHeaderMap() {
		return headerMap;
	}
	
	public void setHeaderMap(Map<String, String> headerMap) {
		this.headerMap = headerMap;
	}
	
	public String getHeader(String name)
	{
		return this.headerMap.get(name);
	}
	
	public void setHeader(String name,String value)
	{
		this.headerMap.put(name,value);
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void setAttribute(String name,String value)
	{
		this.attributeMap.put(name,value);
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
	 
	 public TCPProtocal getOutTcpProtocal() {
		return outTcpProtocal;
	}
	 
	 public void setOutTcpProtocal(TCPProtocal outTcpProtocal) {
		this.outTcpProtocal = outTcpProtocal;
	}
}
