package sam.internetutils;

import java.net.URL;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

public class SimpleDownloadListener implements DownloadListener {
    private final ConnectionConfig config;
    private final Logger logger;
    /**
     * @param internetUtils
     */
    SimpleDownloadListener(ConnectionConfig config) {
        this.config = config;
        this.logger = config.show_download_warnings ? LoggerFactory.getLogger(getClass()) : null;
    }
    
    @Override public boolean extractNameFromWeb(URL url) { 
        return false; 
    }
    @Override public void nameExtracted(URL url, String name) {
    }
    @Override public boolean skipDownload(URL url) {
        return false;
    }
    @Override
    public void contentLength(long contentLength, URL url) {
        if(config.show_download_warnings)
            logger.warn("Content Length = {}\t{}",contentLength,url);
    }
    @Override public void progress(int bytesRead, long totalBytes) {}
    @Override public void compleated(URL url, long totalRead) {}
}