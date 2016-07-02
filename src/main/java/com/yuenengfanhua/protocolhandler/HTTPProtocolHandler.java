package com.yuenengfanhua.protocolhandler;

import com.yuenengfanhua.FileInfo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * Created by gejun on 1/7/16.
 */
@Component
public class HTTPProtocolHandler extends AbstractProtocolHandler{

    @Override
    public String getName() {
        return "HTTPProtocolHandler";
    }

    @Override
    public void process(FileInfo file) throws MalformedURLException, IOException {
        InputStream in = null;
        FileOutputStream fout = null;
        try
        {
            if(file.getUsername().length()>0) {
                Authenticator.setDefault(new MyAuthenticator(file.getUsername(),file.getPassword()));
            }
            in  = new URL(file.getUrl()).openStream();
            fout = new FileOutputStream(downloadDir+FilenameUtils.getName(file.getUrl()));
            super.process(in,fout,file);
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.flush();
            }
        }
    }

    class MyAuthenticator extends Authenticator {
        String username;
        String password;

        public MyAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            // Get information about the request
            String prompt = getRequestingPrompt();
            String hostname = getRequestingHost();
            InetAddress ipaddr = getRequestingSite();
            int port = getRequestingPort();

            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
}
