package org.baffalotech.integration.demo.pojo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "a")
public class DataInput {
	
	private String test;
	
	public String getTest() {
		return test;
	}
	
	public void setTest(String test) {
		this.test = test;
	}
}
