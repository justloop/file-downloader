package com.yuenengfanhua;

import com.yuenengfanhua.protocolhandler.AbstractProtocolHandler;
import com.yuenengfanhua.protocolhandler.FileStatus;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;

/**
 * Created by gejun on 1/7/16.
 */
@Component
@Scope(value="prototype")
@Configuration
public class Downloader implements Runnable{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${download_dir}")
    protected String downloadDir = ""; // download folder

    @Value("${limit:100}")
    private int limit; // download speed limit in kbps

    @Autowired
    DownloadService service;

    private AbstractProtocolHandler handler;
    private FileInfo currentFile;
    private int id = 0;

    public void setId(int id) {
        this.id = id;
    }

    private void log_info(String message) {
        logger.info("[Thread-"+id + "] "+message);
    }

    private void log_error(String file, Exception e) {
        logger.error("[Thread-"+id + "] Error reading file "+file, e);
    }

    @Override
    public void run() {
        FileInfo file = service.get();
        while (file != null) {
            log_info("Assigned " + file + " ...");
            currentFile = file;
            long start = System.currentTimeMillis();
            try {
                service.getStatus().put(file, FileStatus.Downloading);
                URI aURL = new URI(file.getUrl());
                //call different protocol handler by url protocol
                String protocol = aURL.getScheme().toUpperCase();
                Class<?> clazz = Class.forName("com.yuenengfanhua.protocolhandler." + protocol + "ProtocolHandler");
                handler = (AbstractProtocolHandler) clazz.newInstance();
                handler.init(downloadDir,limit);
                log_info("File "+file+" handled by "+handler.getName());
                handler.process(file);
                long end = System.currentTimeMillis();
                log_info("File "+file+" download successful, time elapsed "+(end-start));
                service.getStatus().put(file, FileStatus.Success);
            } catch (Exception e) {
                long end = System.currentTimeMillis();
                log_info("File "+file+" download failed, time elapsed "+(end-start));
                log_error(file.getUrl(), e);
                deleteFile(file.getUrl());
                service.getStatus().put(file, FileStatus.Fail);
            }
            file = service.get();
        }
        log_info("No more files...");
    }

    public void deleteFile(String file) {
        String location = downloadDir+ FilenameUtils.getName(file);
        File deleteFile = new File(location);
        if (deleteFile.exists()) {
            deleteFile.delete();
        }
    }
}
