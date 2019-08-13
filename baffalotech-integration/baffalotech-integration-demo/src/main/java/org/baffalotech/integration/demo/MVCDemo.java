package org.baffalotech.integration.demo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Input;

import org.baffalotech.integration.demo.pojo.DataInput;
import org.baffalotech.integration.demo.pojo.DataOutput;

import com.baffalotech.integration.mvc.annotation.DiscriminatorValue;
import com.baffalotech.integration.mvc.annotation.IController;

@IController(name = "default",discriminator = "test")
public class MVCDemo {

	@DiscriminatorValue(value = "1")
	public DataInput test(DataInput body)
	{
		System.out.println(body.getTest());
		return body;
	}
	
	@DiscriminatorValue(value = "2")
	public DataOutput test2(DataInput body)
	{
		DataOutput dataOutput = new DataOutput();
		dataOutput.setTest(body.getTest());
		dataOutput.setNow(new Date().toLocaleString());
		return dataOutput;
	}
}
