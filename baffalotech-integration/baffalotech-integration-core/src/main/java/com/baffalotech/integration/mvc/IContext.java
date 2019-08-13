package com.baffalotech.integration.mvc;

public interface IContext {

	public Object getPayload();
	
	public Object getRequest();
	
	public void setPayload(Object payload);
	
	public String getConnectorName();
	
	public ContentType getContentType();
	
	public String getEncoding();
	
	/**
	 * 对于输入请求，获取编码后的字串，例如payload是字节数组，则需要按照编码方式生成字符串
	 * @return
	 */
	public String getPayloadString();
}
