package com.yuenengfanhua;

import com.yuenengfanhua.protocolhandler.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

@SpringBootApplication
public class FileDownloaderApplication implements CommandLineRunner {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("classpath:filelist.txt")
	private Resource FileList;

	@Autowired
	DownloadService service;

	public static void main(String[] args) {
		SpringApplication.run(FileDownloaderApplication.class, args);
	}

	@PostConstruct
	public void load(){
		// load the url list from configuration and put it in the queue
		try {
			final InputStream inputStream = FileList.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("//")) // ignore the comment off lines
					continue;

				//put into the queue
				String splitLine[] = line.split(";");
				FileInfo fileInfo = null;
				if(splitLine.length==3) {
					fileInfo = new FileInfo(splitLine[0],splitLine[1],splitLine[2]);
				} else if(splitLine.length>0) {
					fileInfo = new FileInfo(splitLine[0],"","");
				}
				if(fileInfo != null)
					service.put(fileInfo);
			}

			logger.info("Adding "+ service.size()+ " files to queue...");
			br.close();
		}catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void run(String... strings) throws Exception {
		logger.info("Starting file downloaders...");
		service.start();

		service.shutdown();
		logger.info("Everything is done. Summary: ");
		logger.info("---------------------------------------");
		Collection<FileStatus> statusValues = service.getStatus().values();
		logger.info("Status None: " + statusValues.stream().filter(x->x==FileStatus.None).count());
		logger.info("Status Success: " + statusValues.stream().filter(x->x==FileStatus.Success).count());
		logger.info("Status Fail: " + statusValues.stream().filter(x->x==FileStatus.Fail).count());
		logger.info("Status Downloading: " + statusValues.stream().filter(x->x==FileStatus.Downloading).count());
		logger.info("---------------------------------------");
	}
}
