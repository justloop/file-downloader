package com.yuenengfanhua.protocolhandler;

/**
 * Created by gejun on 1/7/16.
 */
public enum FileStatus {
    /**
     * None - File not processed
     * Downloading - File is currently being downloading
     * Success - File has succesfully downloaded
     * Fail - Fail fail to download
     */
    None, Downloading, Success, Fail
}
