package org.baffalotech.integration.demo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baffalotech.integration.mvc.ContentType;
import com.baffalotech.integration.mvc.IContext;
import com.baffalotech.integration.mvc.IFilterChain;
import com.baffalotech.integration.mvc.impl.DefaultContext;
import com.baffalotech.integration.mvc.impl.RequestMappingHandlerMapping;

@RestController
public class TCPTestController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPTestController.class);
	
	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@GetMapping(path = "/test")
	public String test() throws Exception
	{
		try {
			Socket socket = new Socket("localhost", 8082);
			socket.setSoTimeout(30000);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			String testData = "hello world";
			int length = testData.getBytes().length;
			String lengthString = "  " + length;
			String headerString = "abcd" + lengthString;
			outputStream.write(headerString.getBytes());
			outputStream.write(testData.getBytes());
			outputStream.flush();
			StringBuilder sb = new StringBuilder();
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = inputStream.read(buf)) != -1) {
				sb.append(new String(buf, 0, len));
			}
			inputStream.close();
			outputStream.close();
			socket.close();
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exceptio
			LOGGER.error("{}",e);
			throw e;
		}
	}
	
	@GetMapping(path = "/test1")
	public String test1()
	{
		LOGGER.info("dfjdksfjkdasjfkdsf");
		return "hello world"+new Date();
	}
	
	@PostMapping(path = "/test2")
	@ResponseBody
	public String test2(@RequestBody String payload,HttpServletRequest request)
	{
		IContext context = DefaultContext.Builder.newBuilder().payload(payload)
				.connectorName("default")
				.request(request)
				.contentType(ContentType.XML)
				.build();
		IFilterChain filterChain = requestMappingHandlerMapping.getFilterChain(context);
		filterChain.doFilter(context);
		return context.getPayload().toString();
	}
	
	@PostMapping(path = "/test3")
	@ResponseBody
	public String test3(@RequestBody String payload,HttpServletRequest request)
	{
		IContext context = DefaultContext.Builder.newBuilder().payload(payload)
				.connectorName("default1")
				.request(request)
				.contentType(ContentType.JSON)
				.build();
		IFilterChain filterChain = requestMappingHandlerMapping.getFilterChain(context);
		filterChain.doFilter(context);
		return context.getPayload().toString();
	}
}
