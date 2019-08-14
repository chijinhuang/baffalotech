package com.baffalotech.integration.http.netty.mvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.baffalotech.integration.mvc.ContentType;
import com.baffalotech.integration.mvc.IContext;
import com.baffalotech.integration.mvc.IFilterChain;
import com.baffalotech.integration.mvc.impl.DefaultContext;
import com.baffalotech.integration.mvc.impl.RequestMappingHandlerMapping;
import com.baffalotech.integration.util.ApplicationContextUtil;

/**
 * MVC中央控制器
 * @author chijinhuang
 *
 */
public class IDispatchServlet extends HttpServlet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IDispatchServlet.class);
	
	private String encoding = "UTF-8";
	
	private ContentType contentType;
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		InputStream inputStream = req.getInputStream();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while((len=inputStream.read(buf)) != -1)
		{
			byteArrayOutputStream.write(buf, 0, len);
		}
		inputStream.close();
		byteArrayOutputStream.close();
		
		IContext context = DefaultContext.Builder.newBuilder()
				.connectorName(req.getServletContext().getServletContextName())
				.payload(byteArrayOutputStream.toByteArray())
				.encoding(encoding)
				.contentType(contentType)
				.build();
		try {
			RequestMappingHandlerMapping requestMappingHandlerMapping = ApplicationContextUtil.getBean(RequestMappingHandlerMapping.class);
			IFilterChain filterChain = requestMappingHandlerMapping.getFilterChain(context);
			filterChain.doFilter(context);
			OutputStream outputStream = resp.getOutputStream();
			outputStream.write(context.getPayloadString().getBytes(encoding));
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("dispatch error,clause {}",e);
			resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
}
