package com.yuenengfanhua.protocolhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gejun on 1/7/16.
 * 
 * This class to throttle the download speed according to the limit
 */
public class ThrottledInputStream extends InputStream {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final InputStream rawStream;
    private long totalBytesRead;
    private long startTimeMillis;

    private static final int BYTES_PER_KILOBYTE = 1024;
    private static final int MILLIS_PER_SECOND = 1000;
    private final int ratePerMillis;

    public ThrottledInputStream(InputStream rawStream, int limit) {
        this.rawStream = rawStream;
        ratePerMillis = limit * BYTES_PER_KILOBYTE / MILLIS_PER_SECOND;
    }

    private void throttle(int size) {
        if (startTimeMillis == 0) {
            startTimeMillis = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();
        long interval = now - startTimeMillis;
        //see if we are too fast..
        if (interval * ratePerMillis < totalBytesRead + size) { //+size because we are reading size byte
            try {
                final long sleepTime = (totalBytesRead + size) / ratePerMillis - interval; // will most likely only be relevant on the first few passes
                Thread.sleep(Math.max(1, sleepTime));
            } catch (InterruptedException e) {
                logger.warn("Throttle sleep interrupted...");
            }
        }
        totalBytesRead += size;
    }

    @Override
    public int read() throws IOException {
        throttle(1);
        return rawStream.read();
    }

    @Override
    public int read(byte b[]) throws IOException {
        throttle(b.length);
        return rawStream.read(b);
    }
}
