package com.yuenengfanhua.protocolhandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gejun on 1/7/16.
 * 
 * This class to throttle the download speed according to the limit
 */
public class ThrottledInputStream extends InputStream {
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

    private void throttle() {
        if (startTimeMillis == 0) {
            startTimeMillis = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();
        long interval = now - startTimeMillis;
        //see if we are too fast..
        if (interval * ratePerMillis < totalBytesRead + 1) { //+1 because we are reading 1 byte
            try {
                final long sleepTime = ratePerMillis / (totalBytesRead + 1) - interval; // will most likely only be relevant on the first few passes
                Thread.sleep(Math.max(1, sleepTime));
            } catch (InterruptedException e) {//never realized what that is good for :)
            }
        }
        totalBytesRead += 1;
    }

    @Override
    public int read() throws IOException {
        throttle();
        return rawStream.read();
    }

    @Override
    public int read(byte b[]) throws IOException {
        throttle();
        return rawStream.read(b);
    }
}
