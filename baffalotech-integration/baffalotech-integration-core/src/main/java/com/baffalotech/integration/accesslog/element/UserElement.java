package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write remote user that was authenticated (if any), else '-' - %u
 */
public class UserElement implements AccessLogElement {
	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		if (vistor != null) {
			String value = vistor.getRemoteUser();
			if (value != null) {
				buf.append(value);
			} else {
				buf.append('-');
			}
		} else {
			buf.append('-');
		}
	}
}