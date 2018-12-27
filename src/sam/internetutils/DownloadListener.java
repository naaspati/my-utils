package sam.internetutils;

import java.net.URL;

public interface DownloadListener {
    boolean extractNameFromWeb(URL url);
    void nameExtracted(URL url, String name);
    boolean skipDownload(URL url);
    void contentLength(long fileSizeFromWeb, URL url);
    void progress(int bytesRead, long totalBytes);
    void compleated(URL url, long totalRead);
}
