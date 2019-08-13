package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write a specific cookie - %{xxx}c
 */
public class CookieElement implements AccessLogElement {
	private final String header;

	public CookieElement(String header) {
		this.header = header;
	}

	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		String value = "-";

		buf.append(value);
	}
}
