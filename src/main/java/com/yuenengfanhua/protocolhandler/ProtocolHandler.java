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

    public String getName();

    public void process(FileInfo file) throws IOException, URISyntaxException;
}
