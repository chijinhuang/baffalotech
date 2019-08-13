package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write an attribute in the HttpSession - %{xxx}s
 */
public class SessionAttributeElement implements AccessLogElement {
	private final String header;

	public SessionAttributeElement(String header) {
		this.header = header;
	}

	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		buf.append('-');
	}
}