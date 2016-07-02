package com.yuenengfanhua;

import com.yuenengfanhua.protocolhandler.ThrottledInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class ThrottledInputStreamTest {
    private static final Log LOG = LogFactory.getLog(ThrottledInputStreamTest.class);
    private static final int BUFF_SIZE = 1024;

    private enum CB {ONE_C, BUFFER, BUFF_OFFSET}

    @Test
    public void testRead() {
        File tmpFile;
        File outFile;
        try {
            tmpFile = createFile(1024);
            outFile = createFile();

            tmpFile.deleteOnExit();
            outFile.deleteOnExit();

            long maxBandwidth = copyAndAssert(tmpFile, outFile, 0, 1, -1, CB.BUFFER);

            copyAndAssert(tmpFile, outFile, maxBandwidth, 20, 0, CB.BUFFER);
/*
      copyAndAssert(tmpFile, outFile, maxBandwidth, 10, 0, CB.BUFFER);
      copyAndAssert(tmpFile, outFile, maxBandwidth, 50, 0, CB.BUFFER);
*/

            copyAndAssert(tmpFile, outFile, maxBandwidth, 20, 0, CB.BUFF_OFFSET);
/*
      copyAndAssert(tmpFile, outFile, maxBandwidth, 10, 0, CB.BUFF_OFFSET);
      copyAndAssert(tmpFile, outFile, maxBandwidth, 50, 0, CB.BUFF_OFFSET);
*/

            copyAndAssert(tmpFile, outFile, maxBandwidth, 20, 0, CB.ONE_C);
/*
      copyAndAssert(tmpFile, outFile, maxBandwidth, 10, 0, CB.ONE_C);
      copyAndAssert(tmpFile, outFile, maxBandwidth, 50, 0, CB.ONE_C);
*/
        } catch (IOException e) {
            LOG.error("Exception encountered ", e);
        }
    }

    private long copyAndAssert(File tmpFile, File outFile,
                               long maxBandwidth, float factor,
                               int sleepTime, CB flag) throws IOException {
        long bandwidth;
        ThrottledInputStream in;
        long maxBPS = (long) (maxBandwidth / factor);

        if (maxBandwidth == 0) {
            in = new ThrottledInputStream(new FileInputStream(tmpFile));
        } else {
            in = new ThrottledInputStream(new FileInputStream(tmpFile), maxBPS);
        }
        OutputStream out = new FileOutputStream(outFile);
        try {
            if (flag == CB.BUFFER) {
                copyBytes(in, out, BUFF_SIZE);
            } else if (flag == CB.BUFF_OFFSET){
                copyBytesWithOffset(in, out, BUFF_SIZE);
            } else {
                copyByteByByte(in, out);
            }

            LOG.info(in);
            bandwidth = in.getBytesPerSec();
            Assert.assertEquals(in.getTotalBytesRead(), tmpFile.length());
            Assert.assertTrue(in.getBytesPerSec() > maxBandwidth / (factor * 1.2));
            Assert.assertTrue(in.getTotalSleepTime() >  sleepTime || in.getBytesPerSec() <= maxBPS);
        } finally {
            if(in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
        }
        return bandwidth;
    }

    private static void copyBytesWithOffset(InputStream in, OutputStream out, int buffSize)
            throws IOException {

        byte buf[] = new byte[buffSize];
        int bytesRead = in.read(buf, 0, buffSize);
        while (bytesRead >= 0) {
            out.write(buf, 0, bytesRead);
            bytesRead = in.read(buf);
        }
    }

    private static void copyByteByByte(InputStream in, OutputStream out)
            throws IOException {

        int ch = in.read();
        while (ch >= 0) {
            out.write(ch);
            ch = in.read();
        }
    }

    private static void copyBytes(InputStream in, OutputStream out, int buffSize)
            throws IOException {

        byte buf[] = new byte[buffSize];
        int bytesRead = in.read(buf);
        while (bytesRead >= 0) {
            out.write(buf, 0, bytesRead);
            bytesRead = in.read(buf);
        }
    }

    private File createFile(long sizeInKB) throws IOException {
        File tmpFile = createFile();
        writeToFile(tmpFile, sizeInKB);
        return tmpFile;
    }

    private File createFile() throws IOException {
        return File.createTempFile("tmp", "dat");
    }

    private void writeToFile(File tmpFile, long sizeInKB) throws IOException {
        OutputStream out = new FileOutputStream(tmpFile);
        try {
            byte[] buffer = new byte [1024];
            for (long index = 0; index < sizeInKB; index++) {
                out.write(buffer);
            }
        } finally {
            if(out != null)
                out.close();
        }
    }
}