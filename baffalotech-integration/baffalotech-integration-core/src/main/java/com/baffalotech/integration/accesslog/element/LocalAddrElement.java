package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.net.InetAddress;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;
import com.baffalotech.integration.util.IPv6Utils;

/**
     * write local IP address - %A
     */
    public class LocalAddrElement implements AccessLogElement {

        private final String localAddrValue;

        public LocalAddrElement(boolean ipv6Canonical) {
            String init;
            try {
                init = InetAddress.getLocalHost().getHostAddress();
            } catch (Throwable e) {
                init = "127.0.0.1";
            }

            if (ipv6Canonical) {
			localAddrValue = IPv6Utils.canonize(init);
            } else {
                localAddrValue = init;
            }
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, long time,AccessLogElementVistor vistor) {
            buf.append(localAddrValue);
        }
    }