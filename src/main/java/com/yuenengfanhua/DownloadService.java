package com.yuenengfanhua;

import com.yuenengfanhua.protocolhandler.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Created by gejun on 1/7/16.
 */
@Configuration
public class DownloadService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static int MAX_WORKERS = 100;

    @Value("${workers:10}")
    private int workernum; // number of workers

    @Autowired
    private ObjectFactory<Downloader> downloaderObjectFactory;

    //List of downloader
    private ExecutorService executor = Executors.newFixedThreadPool(MAX_WORKERS);

    // queue of to be download resources
    private Queue<FileInfo> queue = new ConcurrentLinkedQueue<FileInfo>();

    // to remember the status of the download files
    private Map<FileInfo, FileStatus> fileStatusMap = new ConcurrentHashMap();

    public void put(FileInfo file) {
        queue.offer(file);
        fileStatusMap.put(file,FileStatus.None);
    }

    public FileInfo get() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

    public void start() {
        logger.info("Start a worker pool of "+ workernum);
        IntStream.range(0, workernum).forEach(
                nbr -> {
                    Downloader downloader = downloaderObjectFactory.getObject();
                    downloader.setId(nbr);
                    executor.execute(downloader);
                }
        );
    }

    public void shutdown() {
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("Error terminating threads "+e.getMessage(),e);
        }
        logger.info("The worker pool was shutdown...");

    }

    public Map<FileInfo, FileStatus> getStatus() {
        return fileStatusMap;
    }
}
