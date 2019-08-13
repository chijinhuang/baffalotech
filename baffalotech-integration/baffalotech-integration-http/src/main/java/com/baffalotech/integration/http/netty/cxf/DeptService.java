package com.baffalotech.integration.http.netty.cxf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface DeptService {

	@WebMethod
	public Dept update(@WebParam(name = "dept")Dept dept);
}
