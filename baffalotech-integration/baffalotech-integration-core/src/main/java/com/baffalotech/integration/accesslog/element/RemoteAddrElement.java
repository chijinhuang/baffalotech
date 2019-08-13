package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write remote IP address - %a
 */
public class RemoteAddrElement implements AccessLogElement {
	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		String value = vistor.getRemoteAddr();
		buf.append(value);
	}
}