package sam.internetutils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleDownloadListener implements DownloadListener {
    /**
     * 
     */
    private InternetUtils internetUtils;
    /**
     * @param internetUtils
     */
    SimpleDownloadListener(InternetUtils internetUtils) {
        this.internetUtils = internetUtils;
    }
    public SimpleDownloadListener() {}
    public void setInternetUtils(InternetUtils internetUtils) {
        this.internetUtils = internetUtils;
    }
    @Override public String getUserAgent() { return this.internetUtils.USER_AGENT; }
    @Override public int getConnectTimeout() { return this.internetUtils.CONNECT_TIMEOUT; }
    @Override public int getReadTimeOut() { return this.internetUtils.READ_TIMEOUT; }
    @Override public boolean extractNameFromWeb(URL url, Path savePath) { 
        return Files.isDirectory(savePath); 
    }
    @Override public Path nameExtracted(URL url, Path savePath, String name) {
        return savePath = savePath.resolve(name);
    }
    @Override public boolean skipDownload(URL url, Path savePath) {
        return this.internetUtils.SKIP_DOWNLOAD_IF_EXISTS && Files.exists(savePath);
    }
    @Override
    public void contentLength(long contentLength, URL url, Path savePath) {
        if(this.internetUtils.SHOW_DOWNLOAD_WARNINGS)
            this.internetUtils.logger.warning(() -> "Content Length = "+contentLength+"\t"+url);
    }
    @Override public void progress(int bytesRead, long totalBytes) {}
    @Override public void compleated(URL url, Path savePath, long totalRead) {}
}