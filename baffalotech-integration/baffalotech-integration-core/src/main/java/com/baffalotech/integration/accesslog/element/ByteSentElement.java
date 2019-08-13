package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write bytes sent, excluding HTTP headers - %b, %B
 */
public class ByteSentElement implements AccessLogElement {
	private final boolean conversion;

	/**
	 * @param conversion <code>true</code> to write '-' instead of 0 - %b.
	 */
	public ByteSentElement(boolean conversion) {
		this.conversion = conversion;
	}

	@Override
	public void addElement(CharArrayWriter buf, Date date, long time, AccessLogElementVistor vistor) {
		// Don't need to flush since trigger for log message is after the
		// response has been committed
		long length = vistor.getBytesWritten();
		if (length <= 0 && conversion) {
			buf.append('-');
		} else {
			buf.append(Long.toString(length));
		}
	}
}