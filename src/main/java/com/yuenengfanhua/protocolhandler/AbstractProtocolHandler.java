package com.yuenengfanhua.protocolhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by gejun on 1/7/16.
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private FileStatus status = FileStatus.None;

    protected String downloadDir; // download folder

    private int limit; // download speed limit in kbps

    public void init(String downloadDir, int limit) {
        this.downloadDir = downloadDir;
        this.limit = limit;
    }

    public FileStatus getStatus() {
        return status;
    }

    protected void process(InputStream inStream, FileOutputStream outStream) throws MalformedURLException, IOException {
        ThrottledInputStream inThrottleStream = new ThrottledInputStream(inStream, limit);
        byte[] buf=new byte[8192];
        int bytesread = 0, bytesBuffered = 0;
        while( (bytesread = inThrottleStream.read( buf )) > -1 ) {
            outStream.write( buf, 0, bytesread );
            bytesBuffered += bytesread;
            if (bytesBuffered > 1024 * 1024) { //flush after 1MB
                bytesBuffered = 0;
                outStream.flush();
            }
        }
    }
}
