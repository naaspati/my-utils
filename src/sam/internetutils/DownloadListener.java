package sam.internetutils;

import java.net.URL;
import java.nio.file.Path;

public interface DownloadListener {
    String getUserAgent();
    int getConnectTimeout();
    int getReadTimeOut();
    boolean extractNameFromWeb(URL url, Path savePath);
    Path nameExtracted(URL url, Path savePath, String name);
    boolean skipDownload(URL url, Path savePath);
    void contentLength(long fileSizeFromWeb, URL url, Path savePath);
    void progress(int bytesRead, long totalBytes);
    void compleated(URL url, Path savePath, long totalRead);
}
