package com.yuenengfanhua;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PreDestroy;
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
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	public static String CONFIG_LOCATION="src/test/resources/filelist.txt";
	public static List<String> lines = Arrays.asList(
			"http://www.google.com.sg/index.html",
			"ftp://demo.wftpserver.com:21/download/jupload.zip;demo-user;demo-user",
			"sftp://test.rebex.net:22/pub/example/readme.txt;demo;password",
			"//ftp://test.com/a.zip"
	);

	@Autowired
	private ApplicationContext applicationContext;

	@Value("${download_dir}")
	protected String downloadDir = ""; // download folder

	@Autowired DownloadService service;

	@BeforeClass
	public static void prepare() throws IOException {
		Path file = Paths.get(CONFIG_LOCATION);
		Files.write(file, lines, Charset.forName("UTF-8"));
	}

	@PreDestroy
	public void init() throws IOException {
		//clear the folder
		File dir = new File(downloadDir);
		FileUtils.cleanDirectory(dir);
	}

	@Test
	public void configTest() {
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
	public void assertFileDownloaded() {
		File file1 = new File(downloadDir+"index.html");
		assertTrue(file1.exists());
	}
}
