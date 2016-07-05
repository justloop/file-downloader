# File Downloader

Project tested in travis CI:

[![Build Status](https://travis-ci.org/yuenengfanhua/file-downloader.svg?branch=master)](https://travis-ci.org/yuenengfanhua/file-downloader)

## Support protocols

Currently FTP, SFTP, HTTP, HTTPS, FILE. It can be easily extended by inherit abstractProtocolHandler class

## URL List File

Program will look for filelist.txt in classpath. Format:

protocol://server:port/path_of_file;username;password

or

protocol://server:port/path_of_file

Program will automatically look for {PROTOCOL}ProtocolHandler by name, and assign the download task to the handler by protocol.

## Configuration

Configuration located inside application.properties:

workers -- number of workers to initialize in the worker pool, or the maximum concurrent download program can handle. Note it can never go beyond 100.

limit -- throttle download speed of each worker, to prevent one worker take up all the bandwidth. Set to a large number if no need for throttling

download_dir -- location of the download folder

## Build and Run

dependency: maven, java 8

use:

mvn install

to generate a bundled jar

run:

java -jar target/filedownloader*.jar
