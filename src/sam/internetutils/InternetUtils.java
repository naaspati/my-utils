package sam.internetutils;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import sam.config.Properties2;
import sam.logging.MyLoggerFactory;
import sam.reference.WeakQueue;

// VERSION = 1.24;
public final class InternetUtils {
	final Logger logger = MyLoggerFactory.logger(InternetUtils.class);
	public  static final int DEFAULT_CONNECT_TIMEOUT;
	public  static final int DEFAULT_READ_TIMEOUT;
	public  static final String DEFAULT_USER_AGENT;
	public  static final int DEFAULT_BUFFER_SIZE;
	public  String USER_AGENT;
	public  int CONNECT_TIMEOUT; 
	public  int READ_TIMEOUT;
	public  boolean SHOW_DOWNLOAD_WARNINGS;
	public  boolean SKIP_DOWNLOAD_IF_EXISTS;
	public  boolean SHOW_WARNINGS;
	public  int BUFFER_SIZE;

	private final WeakQueue<byte[]> buffers;
	
	private static Properties2 config0() {
		try {
			Properties2 p = new Properties2(InternetUtils.class.getResourceAsStream("1509532146836-internet-utils.properties"));
			p.setSystemLookup(true, true);
			return p;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Properties2 config() {
		Properties2 config = config0();
		
		config.putIfAbsent("DEFAULT_CONNECT_TIMEOUT", String.valueOf(DEFAULT_CONNECT_TIMEOUT));
		config.putIfAbsent("DEFAULT_READ_TIMEOUT", String.valueOf(DEFAULT_READ_TIMEOUT));
		config.putIfAbsent("DEFAULT_BUFFER_SIZE", String.valueOf(DEFAULT_BUFFER_SIZE));
		config.putIfAbsent("DEFAULT_USER_AGENT", DEFAULT_USER_AGENT);
		
		return config;
	}

	static {
		Properties2 config = config0();

		config.putIfAbsent("DEFAULT_CONNECT_TIMEOUT", String.valueOf(15*1000));
		config.putIfAbsent("DEFAULT_READ_TIMEOUT", String.valueOf(60*1000));
		config.putIfAbsent("DEFAULT_BUFFER_SIZE", String.valueOf(8*1024));
		config.putIfAbsent("DEFAULT_USER_AGENT", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");

		DEFAULT_CONNECT_TIMEOUT = Integer.parseInt(config.get("DEFAULT_CONNECT_TIMEOUT"));
		DEFAULT_READ_TIMEOUT = Integer.parseInt(config.get("DEFAULT_READ_TIMEOUT"));
		DEFAULT_USER_AGENT = config.get("DEFAULT_USER_AGENT");
		DEFAULT_BUFFER_SIZE = Integer.parseInt(config.get("DEFAULT_BUFFER_SIZE"));
	}

	private String config(String key, String defaultValue, Properties2 config) {
		String s = config.get(key);
		return s != null ? s : defaultValue;
	}
	private int config(String key, int defaultValue, Properties2 config) {
		String s = config.get(key);
		return s != null ? Integer.parseInt(s) : defaultValue; 
	}
	public  InternetUtils(boolean threadSafe) {
		buffers = new WeakQueue<>(threadSafe, () -> new byte[BUFFER_SIZE]);;
		
		Properties2 config = config();

		CONNECT_TIMEOUT = config("CONNECT_TIMEOUT", DEFAULT_CONNECT_TIMEOUT, config); 
		READ_TIMEOUT = config("READ_TIMEOUT", DEFAULT_READ_TIMEOUT, config);
		BUFFER_SIZE = config("BUFFER_SIZE", DEFAULT_BUFFER_SIZE, config);
		USER_AGENT = config("USER_AGENT", DEFAULT_USER_AGENT, config);
		SHOW_DOWNLOAD_WARNINGS = config("SHOW_DOWNLOAD_WARNINGS", "true", config).equalsIgnoreCase("true");
		SKIP_DOWNLOAD_IF_EXISTS = config("SKIP_DOWNLOAD_IF_EXISTS","false", config).equalsIgnoreCase("true");
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

		Path temp = Files.createTempFile(savePath.getFileName().toString(), null);
		byte[] bytes = buffers.poll();
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
			buffers.offer(bytes);
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
