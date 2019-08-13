package com.baffalotech.integration.accesslog.element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>
 * Cache structure for formatted timestamps based on seconds.
 * </p>
 *
 * <p>
 * The cache consists of entries for a consecutive range of seconds. The length
 * of the range is configurable. It is implemented based on a cyclic buffer. New
 * entries shift the range.
 * </p>
 *
 * <p>
 * There is one cache for the CLF format (the access log standard format) and a
 * HashMap of caches for additional formats used by SimpleDateFormat.
 * </p>
 *
 * <p>
 * Although the cache supports specifying a locale when retrieving a formatted
 * timestamp, each format will always use the locale given when the format was
 * first used. New locales can only be used for new formats. The CLF format will
 * always be formatted using the locale <code>en_US</code>.
 * </p>
 *
 * <p>
 * The cache is not threadsafe. It can be used without synchronization via
 * thread local instances, or with synchronization as a global cache.
 * </p>
 *
 * <p>
 * The cache can be created with a parent cache to build a cache hierarchy.
 * Access to the parent cache is threadsafe.
 * </p>
 *
 * <p>
 * This class uses a small thread local first level cache and a bigger
 * synchronized global second level cache.
 * </p>
 */
public class DateFormatCache {

	protected class Cache {

		/* CLF log format */
		private static final String cLFFormat = "dd/MMM/yyyy:HH:mm:ss Z";

		/* Second used to retrieve CLF format in most recent invocation */
		private long previousSeconds = Long.MIN_VALUE;
		/* Value of CLF format retrieved in most recent invocation */
		private String previousFormat = "";

		/* First second contained in cache */
		private long first = Long.MIN_VALUE;
		/* Last second contained in cache */
		private long last = Long.MIN_VALUE;
		/* Index of "first" in the cyclic cache */
		private int offset = 0;
		/* Helper object to be able to call SimpleDateFormat.format(). */
		private final Date currentDate = new Date();

		protected final String cache[];
		private SimpleDateFormat formatter;
		private boolean isCLF = false;

		private Cache parent = null;

		private Cache(Cache parent) {
			this(null, parent);
		}

		private Cache(String format, Cache parent) {
			this(format, null, parent);
		}

		private Cache(String format, Locale loc, Cache parent) {
			cache = new String[cacheSize];
			for (int i = 0; i < cacheSize; i++) {
				cache[i] = null;
			}
			if (loc == null) {
				loc = cacheDefaultLocale;
			}
			if (format == null) {
				isCLF = true;
				format = cLFFormat;
				formatter = new SimpleDateFormat(format, Locale.US);
			} else {
				formatter = new SimpleDateFormat(format, loc);
			}
			formatter.setTimeZone(TimeZone.getDefault());
			this.parent = parent;
		}

		private String getFormatInternal(long time) {

			long seconds = time / 1000;

			/*
			 * First step: if we have seen this timestamp during the previous call, and we
			 * need CLF, return the previous value.
			 */
			if (seconds == previousSeconds) {
				return previousFormat;
			}

			/* Second step: Try to locate in cache */
			previousSeconds = seconds;
			int index = (offset + (int) (seconds - first)) % cacheSize;
			if (index < 0) {
				index += cacheSize;
			}
			if (seconds >= first && seconds <= last) {
				if (cache[index] != null) {
					/* Found, so remember for next call and return. */
					previousFormat = cache[index];
					return previousFormat;
				}

				/* Third step: not found in cache, adjust cache and add item */
			} else if (seconds >= last + cacheSize || seconds <= first - cacheSize) {
				first = seconds;
				last = first + cacheSize - 1;
				index = 0;
				offset = 0;
				for (int i = 1; i < cacheSize; i++) {
					cache[i] = null;
				}
			} else if (seconds > last) {
				for (int i = 1; i < seconds - last; i++) {
					cache[(index + cacheSize - i) % cacheSize] = null;
				}
				first = seconds - (cacheSize - 1);
				last = seconds;
				offset = (index + 1) % cacheSize;
			} else if (seconds < first) {
				for (int i = 1; i < first - seconds; i++) {
					cache[(index + i) % cacheSize] = null;
				}
				first = seconds;
				last = seconds + (cacheSize - 1);
				offset = index;
			}

			/*
			 * Last step: format new timestamp either using parent cache or locally.
			 */
			if (parent != null) {
				synchronized (parent) {
					previousFormat = parent.getFormatInternal(time);
				}
			} else {
				currentDate.setTime(time);
				previousFormat = formatter.format(currentDate);
				if (isCLF) {
					StringBuilder current = new StringBuilder(32);
					current.append('[');
					current.append(previousFormat);
					current.append(']');
					previousFormat = current.toString();
				}
			}
			cache[index] = previousFormat;
			return previousFormat;
		}
	}

	/* Number of cached entries */
	private int cacheSize = 0;

	private final Locale cacheDefaultLocale;
	private final DateFormatCache parent;
	protected final Cache cLFCache;
	private final Map<String, Cache> formatCache = new HashMap<>();

	protected DateFormatCache(int size, Locale loc, DateFormatCache parent) {
		cacheSize = size;
		cacheDefaultLocale = loc;
		this.parent = parent;
		Cache parentCache = null;
		if (parent != null) {
			synchronized (parent) {
				parentCache = parent.getCache(null, null);
			}
		}
		cLFCache = new Cache(parentCache);
	}

	private Cache getCache(String format, Locale loc) {
		Cache cache;
		if (format == null) {
			cache = cLFCache;
		} else {
			cache = formatCache.get(format);
			if (cache == null) {
				Cache parentCache = null;
				if (parent != null) {
					synchronized (parent) {
						parentCache = parent.getCache(format, loc);
					}
				}
				cache = new Cache(format, loc, parentCache);
				formatCache.put(format, cache);
			}
		}
		return cache;
	}

	public String getFormat(long time) {
		return cLFCache.getFormatInternal(time);
	}

	public String getFormat(String format, Locale loc, long time) {
		return getCache(format, loc).getFormatInternal(time);
	}
}