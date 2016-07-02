package com.yuenengfanhua.protocolhandler;

import com.yuenengfanhua.FileInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by gejun on 2/7/16.
 */
public class FTPSProtocolHandler extends AbstractProtocolHandler{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getName() {
        return "FTPProtocolHandler";
    }

    @Override
    public void process(FileInfo file) throws IOException, URISyntaxException {
        URI aUrl = new URI(file.getUrl());
        InputStream in = null;
        FileOutputStream fout = null;
        FTPSClient client = null;
        try {
            client = getFTPSClient(aUrl.getHost(), file.getUsername(), file.getPassword(), aUrl.getPort());
            in = client.retrieveFileStream(aUrl.getPath());
            fout = new FileOutputStream(downloadDir+ FilenameUtils.getName(file.getUrl()));
            super.process(in,fout,file);
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.flush();
            }
            if (client != null) {
                client.logout();
                client.disconnect();
            }
        }
    }

    public FTPSClient getFTPSClient(String ftpHost, String ftpPassword,
                                         String ftpUserName, int ftpPort) throws IOException {
        if(ftpUserName.length()<1) //anonymous login
            ftpUserName = "anonymous";
        FTPSClient ftpClient  = new FTPSClient("SSL");
        ftpClient.connect(ftpHost, ftpPort);// connect to FTP Server
        if(!ftpClient.login(ftpUserName, ftpPassword)){
            // Login to FTP server
            throw new IOException("not able to conect to server "+ftpHost);
        }
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            logger.error("Username or Password wrong, disconnect...");
            ftpClient.disconnect();
            throw new IOException("username password not correct!");
        }
        ftpClient.enterLocalPassiveMode();
        ftpClient.enterRemotePassiveMode();
        ftpClient.setBufferSize(8192);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // BINARY TYPE NEVER FAIL
        ftpClient.setAutodetectUTF8(true);
        return ftpClient;
    }
}
