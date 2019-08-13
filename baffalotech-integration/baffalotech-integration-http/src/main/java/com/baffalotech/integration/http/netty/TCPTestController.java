package com.baffalotech.integration.http.netty;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TCPTestController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPTestController.class);

	@GetMapping(path = "/test")
	public String test() throws Exception
	{
		try {
			Socket socket = new Socket("localhost", 8085);
			socket.setSoTimeout(30000);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			String testData = "hello world";
			int length = testData.getBytes().length;
			String lengthString = "  " + length;
			String headerString = "abcdabcd" + lengthString;
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
		return "hello world"+new Date();
	}
}
