package org.baffalotech.integration.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.baffalotech.integration.annotation.Connector;
import com.baffalotech.integration.api.Container;
import com.baffalotech.integration.tcp.TCPRequest;
import com.baffalotech.integration.tcp.TCPResponse;
import com.baffalotech.integration.tcp.handler.TCPHandler.TCPRootHandler;

@Connector(name = "hbnx")
public class TestRootHandler implements TCPRootHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestRootHandler.class);

	@Autowired
	private Container container;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public void hanle(TCPRequest tcpRequest, TCPResponse tcpResponse) {
		// TODO Auto-generated method stub
		LOGGER.info("result:{}","fkdslfjsakdlf");
		ResponseEntity<String> result = restTemplate.getForEntity("http://demo/test1", String.class);
		LOGGER.info("result:{}",result.getStatusCode());
		tcpResponse.setData(result.getBody().getBytes());
	}

}
