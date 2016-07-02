package com.yuenengfanhua.protocolhandler;

import com.yuenengfanhua.FileInfo;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by gejun on 1/7/16.
 */
@Scope(value="prototype")
public interface ProtocolHandler {

    /**
     * Get the name of Protocol Hnalder
     * @return Name
     */
    public String getName();

    /**
     * @param file the file to be processed
     * Extend this method to download file
     */
    public void process(FileInfo file) throws IOException, URISyntaxException;
}
