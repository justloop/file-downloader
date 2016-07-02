package com.yuenengfanhua.protocolhandler;

import com.yuenengfanhua.FileInfo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by gejun on 1/7/16.
 */
@Component
public class FileProtocolHandler extends AbstractProtocolHandler{
    @Override
    public String getName() {
        return "FileProtocolHandler";
    }

    @Override
    public void process(FileInfo file) throws MalformedURLException, IOException {
        FileInputStream in = null;
        FileOutputStream fout = null;
        try
        {
            in  = new FileInputStream(file.getUrl());
            fout = new FileOutputStream(downloadDir+ FilenameUtils.getName(file.getUrl()));
            super.process(in,fout);
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
}
