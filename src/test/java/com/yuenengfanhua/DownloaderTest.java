package com.yuenengfanhua;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by gejun on 2/7/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DownloaderTest {
    public static String FILE_LOCATION="test.txt";
    public static List<String> lines = Arrays.asList(
            "http://mirror.nus.edu.sg/centos/6.8/storage/x86_64/gluster-3.6/glusterfs-3.6.9-1.el6.x86_64.rpm",
            "ftp://demo.wftpserver.com:21/download/jupload.zip;demo-user;demo-user",
            "sftp://test.rebex.net:22/pub/example/readme.txt;demo;password",
            "//ftp://test.com/a.zip"
    );

    @InjectMocks
    private static Downloader downloder = new Downloader();

    @Value("${download_dir}")
    protected String downloadDir = ""; // download folder

    @Mock
    DownloadService service;

    @BeforeClass
    public static void prepare() {
        downloder.setId(0);
    }

    @Test
    public void deleteFileTest() throws IOException {
        Path file = Paths.get(downloadDir+FILE_LOCATION);
        Files.write(file, lines, Charset.forName("UTF-8"));

        File file2 = new File(downloadDir+FILE_LOCATION);
        assertTrue(file2.exists());

        downloder.deleteFile(FILE_LOCATION);
        assertTrue(!file2.exists());
    }

}
