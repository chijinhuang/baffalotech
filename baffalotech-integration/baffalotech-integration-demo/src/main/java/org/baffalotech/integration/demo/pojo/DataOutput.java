package org.baffalotech.integration.demo.pojo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "b")
public class DataOutput {

	private String test;
	
	private String now;
	
	public String getTest() {
		return test;
	}
	
	public void setTest(String test) {
		this.test = test;
	}
	
	public String getNow() {
		return now;
	}
	
	public void setNow(String now) {
		this.now = now;
	}
}
