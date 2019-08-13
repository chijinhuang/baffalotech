package org.baffalotech.integration.demo;

import java.util.HashMap;
import java.util.Map;

import org.baffalotech.integration.demo.pojo.JsonDataInput;

import com.baffalotech.integration.mvc.annotation.DiscriminatorValue;
import com.baffalotech.integration.mvc.annotation.IController;

@IController(name = "default1",discriminator = "test")
public class MVCJsonDemo {

	@DiscriminatorValue(value = "1")
	public Map<String, String> test(JsonDataInput data)
	{
		Map<String, String> result = new HashMap<String, String>();
		result.put("dkjfkdas", "ksdjfkdsajf");
		result.put("now", "djfkdlsjfkdsjfkldsjf");
		return result;
	}
	
	@DiscriminatorValue(value = "2")
	public Map<String, String> test1(JsonDataInput data)
	{
		Map<String, String> result = new HashMap<String, String>();
		result.put("dkjfkdas", "ksdjfkdsajfdfkajfieakutiejfdkshgsdkhgiodjgioreugjfklgdhfgiosdjgklsfdjgds");
		result.put("now", "djfkdlsjfkdsjfkldsjf");
		return result;
	}
}
