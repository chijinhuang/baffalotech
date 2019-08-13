package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write remote host name - %h
 */
public class HostElement implements AccessLogElement {
	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		String value = vistor.getHost();

		if (value == null || value.length() == 0) {
			value = "-";
		}
		buf.append(value);
	}
}