package com.baffalotech.integration.demo.cxf;

import javax.jws.WebService;


@WebService(targetNamespace="http://service.webservicedemo.dbgo.com1/",endpointInterface = "com.baffalotech.integration.http.netty.cxf.DeptService")
public class DeptServiceImpl implements DeptService {

	@Override
	public Dept update(Dept dept) {
		// TODO Auto-generated method stub
		return dept;
	}

}
