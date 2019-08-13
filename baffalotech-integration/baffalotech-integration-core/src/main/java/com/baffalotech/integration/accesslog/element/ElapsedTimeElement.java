package com.baffalotech.integration.accesslog.element;

import java.io.CharArrayWriter;
import java.util.Date;

import com.baffalotech.integration.accesslog.AccessLogElement;
import com.baffalotech.integration.accesslog.AccessLogElementVistor;

/**
     * write time taken to process the request - %D, %T
     */
    public class ElapsedTimeElement implements AccessLogElement {
        private final boolean millis;

        /**
         * @param millis <code>true</code>, write time in millis - %D,
         * if <code>false</code>, write time in seconds - %T
         */
        public ElapsedTimeElement(boolean millis) {
            this.millis = millis;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, long time,AccessLogElementVistor vistor) {
            if (millis) {
                buf.append(Long.toString(time));
            } else {
                // second
                buf.append(Long.toString(time / 1000));
                buf.append('.');
                int remains = (int) (time % 1000);
                buf.append(Long.toString(remains / 100));
                remains = remains % 100;
                buf.append(Long.toString(remains / 10));
                buf.append(Long.toString(remains % 10));
            }
        }
    }