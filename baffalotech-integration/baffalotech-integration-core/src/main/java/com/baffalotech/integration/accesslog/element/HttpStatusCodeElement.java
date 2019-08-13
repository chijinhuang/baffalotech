package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write HTTP status code of the response - %s
 */
public class HttpStatusCodeElement implements AccessLogElement {
	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		if (vistor != null) {
			// This approach is used to reduce GC from toString conversion
			int status = vistor.getStatus();
			if (100 <= status && status < 1000) {
				buf.append((char) ('0' + (status / 100))).append((char) ('0' + ((status / 10) % 10)))
						.append((char) ('0' + (status % 10)));
			} else {
				buf.append(Integer.toString(status));
			}
		} else {
			buf.append('-');
		}
	}
}