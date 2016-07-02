package com.yuenengfanhua.protocolhandler;

import com.jcraft.jsch.*;
import com.yuenengfanhua.FileInfo;
import org.apache.commons.io.FilenameUtils;
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
public class SFTPProtocolHandler extends AbstractProtocolHandler{
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
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(file.getUsername(),aUrl.getHost(),aUrl.getPort());
            session.setPassword(file.getPassword());
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            in = channelSftp.get(aUrl.getPath());
            fout = new FileOutputStream(downloadDir+ FilenameUtils.getName(file.getUrl()));
            super.process(in,fout);
        } catch (SftpException | JSchException e) {
            logger.error("Error with Jsch client "+ e.getMessage(), e);
            throw new IOException("Error with Jsch client");
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.flush();
            }
            if(channelSftp != null) {
                channelSftp.disconnect();
            }
            if(channel != null) {
                channel.disconnect();
            }
            if(session != null) {
                session.disconnect();
            }
        }
    }

}
