package com.yuenengfanhua;

import com.yuenengfanhua.protocolhandler.FileStatus;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FileDownloaderApplication.class)
public class FileDownloaderApplicationTests {
	private static final Logger logger = LoggerFactory.getLogger(FileDownloaderApplicationTests.class);

	public static String CONFIG_LOCATION="src/test/resources/filelist.txt";
	public static List<String> lines = Arrays.asList(
			"http://example.com/index.html",
			"ftp://demo.wftpserver.com:21/download/jupload.zip;demo-user;demo-user",
			"sftp://test.rebex.net:22/pub/example/readme.txt;demo;password",
			"//http://test.com/b.zip",
			"//ftp://test.com:21/a.zip"
	);

	@Value("${download_dir}")
	protected String downloadDir = ""; // download folder

	@Autowired DownloadService service;

	@BeforeClass
	public static void cleanForNext() throws IOException {
		Path file = Paths.get(CONFIG_LOCATION);
		Files.write(file, lines, Charset.forName("UTF-8"));

		logger.info("Clearing testing directory test_download");
		File dir = new File("test_download");
		FileUtils.cleanDirectory(dir);
	}

	@Test
	public void configTest() {
		logger.info("configTest");
		assertEquals(3,service.getStatus().size());

		Collection<FileInfo> fileInfos = service.getStatus().keySet();
		FileInfo file1 = new FileInfo(lines.get(0),"","");
		assertTrue(fileInfos.contains(file1));
		String file2Arr[] = lines.get(1).split(";");
		FileInfo file2 = new FileInfo(file2Arr[0],file2Arr[1],file2Arr[2]);
		assertTrue(fileInfos.contains(file2));
		String file3Arr[] = lines.get(2).split(";");
		FileInfo file3 = new FileInfo(file3Arr[0],file3Arr[1],file3Arr[2]);
		assertTrue(fileInfos.contains(file3));
	}

	@Test
	public void taskFinishTest() {
		logger.info("taskFinishTest");
		assertEquals(null,service.get());
	}

	@Test
	public void assertFileDownloaded() {
		logger.info("assertFileDownloaded");
		// assert the file specified are all downloaded
		File file1 = new File(downloadDir+"index.html");
		assertTrue(file1.exists());

		File file2 = new File(downloadDir+"readme.txt");
		assertTrue(file2.exists());

		File file3 = new File(downloadDir+"jupload.zip");
		assertTrue(file3.exists());
	}

	@Test
	public void assertDownloadSuccess() {
		logger.info("assertDownloadSuccess");
		Collection<FileStatus> statusList = service.getStatus().values();
		assertEquals(3, statusList.stream().filter(x->x==FileStatus.Success).count());
		//assertEquals(1, statusList.stream().filter(x->x==FileStatus.Fail).count());
	}
}
