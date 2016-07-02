package com.yuenengfanhua.protocolhandler;

import org.springframework.stereotype.Component;

/**
 * Created by gejun on 1/7/16.
 */
@Component
public class HTTPSProtocolHandler extends HTTPProtocolHandler{

    @Override
    public String getName() {
        return "HTTPSProtocolHandler";
    }

}
