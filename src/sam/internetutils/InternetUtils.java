package sam.internetutils;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public final class InternetUtils {
    public static final double VERSION = 1.22;
    public static final String REQUIRED = "1509532146836-internet-utils.properties";

    final Logger logger = Logger.getLogger(InternetUtils.class.getSimpleName());
    public  final int DEFAULT_CONNECT_TIMEOUT;
    public  final int DEFAULT_READ_TIMEOUT;
    public  final String DEFAULT_USER_AGENT;
    public  String USER_AGENT;
    public  int CONNECT_TIMEOUT; 
    public  int READ_TIMEOUT;
    public  boolean SHOW_DOWNLOAD_WARNINGS;
    public  boolean SKIP_DOWNLOAD_IF_EXISTS;
    public  boolean SHOW_WARNINGS;
    public  int BUFFER_SIZE;

    public  InternetUtils() {
        Map<String, String> map = new LinkedHashMap<>();

        try(InputStream is = getClass().getResourceAsStream("1509532146836-internet-utils.properties");
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                ) {
            br.lines()
            .filter(s -> 
            s.indexOf('=') > 0 && 
            s.trim().indexOf('#') != 0
                    ).collect(Collectors.toMap(s -> s.substring(0, s.indexOf('=')).trim(), s -> s.substring(s.indexOf('=')+1).trim(), (o,n) -> n, () -> map));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        map.putIfAbsent("DEFAULT_CONNECT_TIMEOUT", String.valueOf(15*1000));
        map.putIfAbsent("DEFAULT_READ_TIMEOUT", String.valueOf(60*1000));
        map.putIfAbsent("DEFAULT_BUFFER_SIZE", String.valueOf(8*1024));
        map.putIfAbsent("DEFAULT_USER_AGENT", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");

        DEFAULT_CONNECT_TIMEOUT = Integer.parseInt(map.get("DEFAULT_CONNECT_TIMEOUT"));
        DEFAULT_READ_TIMEOUT = Integer.parseInt(map.get("DEFAULT_READ_TIMEOUT"));
        DEFAULT_USER_AGENT = map.get("DEFAULT_USER_AGENT");

        BiFunction<String, String, String> to = (key, defaultValue) -> {
            String value = map.get(key);
            String v2 = map.get(value);
            return v2 != null ? v2 : value != null ? value : map.get(defaultValue);
        };

        CONNECT_TIMEOUT = Integer.parseInt(to.apply("CONNECT_TIMEOUT", "DEFAULT_CONNECT_TIMEOUT")); 
        READ_TIMEOUT = Integer.parseInt(to.apply("READ_TIMEOUT", "DEFAULT_READ_TIMEOUT"));
        BUFFER_SIZE = Integer.parseInt(to.apply("BUFFER_SIZE", "DEFAULT_BUFFER_SIZE"));
        USER_AGENT = to.apply("USER_AGENT", "DEFAULT_USER_AGENT");
        SHOW_DOWNLOAD_WARNINGS = map.getOrDefault("SHOW_DOWNLOAD_WARNINGS", "true").equalsIgnoreCase("true");
        SKIP_DOWNLOAD_IF_EXISTS = map.getOrDefault("SKIP_DOWNLOAD_IF_EXISTS","false").equalsIgnoreCase("true");
    }
    public final SimpleDownloadListener DEFAULT_LISTENER = new SimpleDownloadListener(this);

    /**
     * {@link InternetUtils#download(URL, Path, DownloadListener)}
     */
    public  Path download(String url, String savePath) throws MalformedURLException, IOException {
        return download(new URL(url), Paths.get(savePath));
    }
    /**
     * {@link InternetUtils#download(URL, Path, DownloadListener)}
     */
    public  Path download(URL url, Path savePath) throws IOException {
        return download(url, savePath, DEFAULT_LISTENER);
    }

    private WeakReference<byte[]> buffer = new WeakReference<byte[]>(null);
    private boolean inUse = false;

    private byte[] getBuffer() {
        if(inUse)
            return new byte[BUFFER_SIZE];

        inUse = true;
        byte[] w = buffer.get();

        if(w != null) 
            return w;

        buffer = new WeakReference<byte[]>(w = new byte[BUFFER_SIZE]);
        return w;
    }
    private void setBuffer() {
        inUse = false;
    }

    /**
     * download a file 
     * @param url from where file will be downloaded 
     * @param savePath where file donwloaded file will be saved
     * @param connectTimeout maximum time to wait for connection
     * @param readTimeout maximum read time from connection
     * @return returns path to file where it has been saved
     * @throws IOException
     * 
     */
    public  Path download(URL url, Path savePath, DownloadListener listener) throws IOException {
        URLConnection con = url.openConnection();
        con.setRequestProperty("User-Agent", listener.getUserAgent());
        con.setConnectTimeout(listener.getConnectTimeout());
        con.setReadTimeout(listener.getReadTimeOut());
        con.connect();

        if(listener.extractNameFromWeb(url, savePath)){
            String name = con.getHeaderField("Content-Disposition");

            if(name != null)
                name = name.substring(name.indexOf('"') + 1, name.lastIndexOf('"')).trim();

            if(name == null || name.isEmpty()){
                name = new File(url.getFile()).getName();
                try {
                    String ext = "." + new MimeType(con.getHeaderField("Content-Type")).getSubType(); 
                    if(!(name.endsWith(ext) || (name.endsWith(".jpg") && ext.endsWith(".jpeg")) || name.endsWith(".jpeg") && ext.endsWith(".jpg")))
                        name += ext;
                } catch (MimeTypeParseException|NullPointerException e) {} 
            }
            savePath = listener.nameExtracted(url, savePath, name.indexOf('?') < 0 ? name : name.substring(0, name.indexOf('?')));
        }
        if(listener.skipDownload(url, savePath))
            return savePath;

        Path temp = savePath.resolveSibling(savePath.getFileName()+".tmp");
        byte[] bytes = getBuffer();
        long conFileSize = con.getContentLengthLong();
        listener.progress(0, conFileSize);
        
        int totalRead = 0;

        try(InputStream is = con.getInputStream();
                OutputStream os = Files.newOutputStream(temp, CREATE, WRITE, TRUNCATE_EXISTING);
                ) {
            int n = 0;

            while((n = is.read(bytes)) != -1) {
                os.write(bytes, 0, n);
                totalRead += n;
                listener.progress(totalRead, conFileSize);
            }
        } finally {
            setBuffer();
        }

        Files.move(temp, savePath, StandardCopyOption.REPLACE_EXISTING);
        listener.compleated(url, savePath, totalRead);
        return savePath;
    }
    public  InputStream openUrlInputStream(URL url) throws IOException {
        URLConnection con = url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
        con.setConnectTimeout(CONNECT_TIMEOUT);
        con.setReadTimeout(READ_TIMEOUT);
        return con.getInputStream();
    }
}
