package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;
import java.util.Locale;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
 * write date and time, in configurable format (default CLF) - %t or %{format}t
 */
public class DateAndTimeElement implements AccessLogElement {

	/**
	 * Format prefix specifying request start time
	 */
	private static final String requestStartPrefix = "begin";

	/**
	 * Format prefix specifying response end time
	 */
	private static final String responseEndPrefix = "end";

	/**
	 * Separator between optional prefix and rest of format
	 */
	private static final String prefixSeparator = ":";

	/**
	 * Special format for seconds since epoch
	 */
	private static final String secFormat = "sec";

	/**
	 * Special format for milliseconds since epoch
	 */
	private static final String msecFormat = "msec";

	/**
	 * Special format for millisecond part of timestamp
	 */
	private static final String msecFractionFormat = "msec_frac";

	/**
	 * The patterns we use to replace "S" and "SSS" millisecond formatting of
	 * SimpleDateFormat by our own handling
	 */
	private static final String msecPattern = "{#}";
	private static final String trippleMsecPattern = msecPattern + msecPattern + msecPattern;
	
	 /**
     * The size of our global date format cache
     */
    private static final int globalCacheSize = 300;

    /**
     * The size of our thread local date format cache
     */
    private static final int localCacheSize = 60;
	/**
     * Global date format cache.
     */
    private static final DateFormatCache globalDateCache =
            new DateFormatCache(globalCacheSize, Locale.getDefault(), null);
	 /**
     * Thread local date format cache.
     */
    private static final ThreadLocal<DateFormatCache> localDateCache =
            new ThreadLocal<DateFormatCache>() {
        @Override
        protected DateFormatCache initialValue() {
            return new DateFormatCache(localCacheSize, Locale.getDefault(), globalDateCache);
        }
    };

	/* Our format description string, null if CLF */
	private final String format;
	/* Whether to use begin of request or end of response as the timestamp */
	private final boolean usesBegin;
	/* The format type */
	private final FormatType type;
	/* Whether we need to postprocess by adding milliseconds */
	private boolean usesMsecs = false;

	public DateAndTimeElement() {
		this(null);
	}

	/**
	 * Replace the millisecond formatting character 'S' by some dummy characters in
	 * order to make the resulting formatted time stamps cacheable. We replace the
	 * dummy chars later with the actual milliseconds because that's relatively
	 * cheap.
	 */
	private String tidyFormat(String format) {
		boolean escape = false;
		StringBuilder result = new StringBuilder();
		int len = format.length();
		char x;
		for (int i = 0; i < len; i++) {
			x = format.charAt(i);
			if (escape || x != 'S') {
				result.append(x);
			} else {
				result.append(msecPattern);
				usesMsecs = true;
			}
			if (x == '\'') {
				escape = !escape;
			}
		}
		return result.toString();
	}

	public DateAndTimeElement(String header) {
		String format = header;
		boolean usesBegin = false;
		FormatType type = FormatType.CLF;

		if (format != null) {
			if (format.equals(requestStartPrefix)) {
				usesBegin = true;
				format = "";
			} else if (format.startsWith(requestStartPrefix + prefixSeparator)) {
				usesBegin = true;
				format = format.substring(6);
			} else if (format.equals(responseEndPrefix)) {
				usesBegin = false;
				format = "";
			} else if (format.startsWith(responseEndPrefix + prefixSeparator)) {
				usesBegin = false;
				format = format.substring(4);
			}
			if (format.length() == 0) {
				type = FormatType.CLF;
			} else if (format.equals(secFormat)) {
				type = FormatType.SEC;
			} else if (format.equals(msecFormat)) {
				type = FormatType.MSEC;
			} else if (format.equals(msecFractionFormat)) {
				type = FormatType.MSEC_FRAC;
			} else {
				type = FormatType.SDF;
				format = tidyFormat(format);
			}
		}
		this.format = format;
		this.usesBegin = usesBegin;
		this.type = type;
	}

	@Override
	public void addElement(CharArrayWriter buf, Date date, long time,AccessLogElementVistor vistor) {
		long timestamp = date.getTime();
		long frac;
		if (usesBegin) {
			timestamp -= time;
		}
		/*
		 * Implementation note: This is deliberately not implemented using switch. If a
		 * switch is used the compiler (at least the Oracle one) will use a synthetic
		 * class to implement the switch. The problem is that this class needs to be
		 * pre-loaded when using a SecurityManager and the name of that class will
		 * depend on any anonymous inner classes and any other synthetic classes. As
		 * such the name is not constant and keeping the pre-loading up to date as the
		 * name changes is error prone.
		 */
		if (type == FormatType.CLF) {
			buf.append(localDateCache.get().getFormat(timestamp));
		} else if (type == FormatType.SEC) {
			buf.append(Long.toString(timestamp / 1000));
		} else if (type == FormatType.MSEC) {
			buf.append(Long.toString(timestamp));
		} else if (type == FormatType.MSEC_FRAC) {
			frac = timestamp % 1000;
			if (frac < 100) {
				if (frac < 10) {
					buf.append('0');
					buf.append('0');
				} else {
					buf.append('0');
				}
			}
			buf.append(Long.toString(frac));
		} else {
			// FormatType.SDF
			String temp = localDateCache.get().getFormat(format, Locale.getDefault(), timestamp);
			if (usesMsecs) {
				frac = timestamp % 1000;
				StringBuilder trippleMsec = new StringBuilder(4);
				if (frac < 100) {
					if (frac < 10) {
						trippleMsec.append('0');
						trippleMsec.append('0');
					} else {
						trippleMsec.append('0');
					}
				}
				trippleMsec.append(frac);
				temp = temp.replace(trippleMsecPattern, trippleMsec);
				temp = temp.replace(msecPattern, Long.toString(frac));
			}
			buf.append(temp);
		}
	}
}