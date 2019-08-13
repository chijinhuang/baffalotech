package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write user session ID - %S
 */
public class SessionIdElement implements AccessLogElement {
	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		if (vistor == null) {
			buf.append('-');
		} else {
			String sessionId = vistor.getSessionId();
			if (sessionId == null) {
				buf.append('-');
			} else {
				buf.append(sessionId);
			}
		}
	}
}