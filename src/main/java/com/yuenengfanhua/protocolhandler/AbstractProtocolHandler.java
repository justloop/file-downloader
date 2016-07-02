package com.yuenengfanhua.protocolhandler;

import com.yuenengfanhua.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by gejun on 1/7/16.
 *
 * Combine the common functionality of Protocol handlers
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private FileStatus status = FileStatus.None;

    protected String downloadDir; // download folder

    private int limit; // download speed limit in kbps

    /**
     * @param downloadDir directory of all downloaded files
     * @param limit download speed limit
     * initailize the class, set download directory and download speed limit
     */
    public void init(String downloadDir, int limit) {
        this.downloadDir = downloadDir;
        this.limit = limit;
    }

    /**
     * Get file status of the current file
     * @return FileStatus to be returned
     */
    public FileStatus getStatus() {
        return status;
    }

    /**
     * @param inStream inputStream, passed by inherited class
     * @param outStream outputStream, here refers to output file stream
     * @param info fileInfo, used for logging progress of this file
     *
     * This method contains the common function of all handler, reading from input stream and output to file output stream.
     */
    protected void process(InputStream inStream, FileOutputStream outStream, FileInfo info) throws MalformedURLException, IOException {
        ThrottledInputStream inThrottleStream = new ThrottledInputStream(inStream, limit);
        byte[] buf=new byte[8192]; // having a small buffer and write in stream to forbid memory flow
        int bytesread = 0, bytesBuffered = 0;
        int round = 0;
        while( (bytesread = inThrottleStream.read( buf )) > -1 ) {
            outStream.write( buf, 0, bytesread );
            bytesBuffered += bytesread;
            if (bytesBuffered > 1024 * 1024) { //flush after 1MB
                bytesBuffered = 0;
                outStream.flush();
                round++;
                if(logger.isDebugEnabled()) {
                    logger.debug("[Progress] -- "+ info.getUrl()+" | "+round+"MB~");
                }
            }
        }
        outStream.flush();
    }
}
