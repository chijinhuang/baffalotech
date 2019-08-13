package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * Write connection status when response is completed - %X
 */
public class ConnectionStatusElement implements AccessLogElement {
	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		// Unknown connection status
		buf.append('?');
	}
}