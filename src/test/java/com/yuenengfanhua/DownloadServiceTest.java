package com.yuenengfanhua;

import com.yuenengfanhua.protocolhandler.FileStatus;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by gejun on 2/7/16.
 */
public class DownloadServiceTest {
    public static List<String> lines = Arrays.asList(
            "http://mirror.nus.edu.sg/centos/6.8/storage/x86_64/gluster-3.6/glusterfs-3.6.9-1.el6.x86_64.rpm",
            "ftp://demo.wftpserver.com:21/download/jupload.zip;demo-user;demo-user",
            "sftp://test.rebex.net:22/pub/example/readme.txt;demo;password"
    );

    private static DownloadService service = new DownloadService();

    private static void putTests() {
        FileInfo file1 = new FileInfo(lines.get(0),"","");
        String file2Arr[] = lines.get(1).split(";");
        FileInfo file2 = new FileInfo(file2Arr[0],file2Arr[1],file2Arr[2]);
        String file3Arr[] = lines.get(2).split(";");
        FileInfo file3 = new FileInfo(file3Arr[0],file3Arr[1],file3Arr[2]);

        service.put(file1);
        service.put(file2);
        service.put(file3);
    }

    @BeforeClass
    public static void prepare() {
        putTests();
    }


    @Test
    public void testPutGet() {
        DownloadService localService = new DownloadService();
        FileInfo file1 = new FileInfo(lines.get(0),"","");
        String file2Arr[] = lines.get(1).split(";");
        FileInfo file2 = new FileInfo(file2Arr[0],file2Arr[1],file2Arr[2]);
        String file3Arr[] = lines.get(2).split(";");
        FileInfo file3 = new FileInfo(file3Arr[0],file3Arr[1],file3Arr[2]);

        localService.put(file1);
        localService.put(file2);
        localService.put(file3);

        assertEquals(3,localService.size());

        FileInfo info1 = localService.get();
        assertEquals(file1,info1);
        FileInfo info2 = localService.get();
        assertEquals(file2,info2);
        FileInfo info3 = localService.get();
        assertEquals(file3,info3);

        assertEquals(0,localService.size());
    }

    @Test
    public void initStatusTest() {
        Collection<FileStatus> statusList = service.getStatus().values();
        assertEquals(3,statusList.stream().filter(x->x==FileStatus.None).count());
    }


}
