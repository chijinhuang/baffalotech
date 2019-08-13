package com.baffalotech.integration.http.netty.cxf;

import java.io.Serializable;

public class Dept implements Serializable {

	private static final long serialVersionUID = -7060996680879780433L;

	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
