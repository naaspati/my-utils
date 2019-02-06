package sam.internetutils;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
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
import java.util.concurrent.atomic.AtomicInteger;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import sam.myutils.MyUtilsPath;
import sam.reference.WeakPool;

// VERSION = 1.24;
public final class InternetUtils {
	private static final AtomicInteger COUNTER = new AtomicInteger(0);
	private static final WeakPool<byte[]> BUFFERS = new WeakPool<>(true, () -> new byte[ConnectionConfig.DEFAULT_BUFFER_SIZE]);

	public static final ConnectionConfig _DEFAULT_CONFIG = new ConnectionConfig();
	public static final DownloadListener _DEFAULT_LISTENER = new SimpleDownloadListener(_DEFAULT_CONFIG);
	
	private final ConnectionConfig config;
	private final DownloadListener listener;
	
	public InternetUtils() {
		this.config = _DEFAULT_CONFIG;
		this.listener = _DEFAULT_LISTENER;
	}

	public InternetUtils(ConnectionConfig config, DownloadListener listener) {
		this.config = config;
		this.listener = listener;
	}
	/**
	 * {@link InternetUtils#download(URL, Path, DownloadListener)}
	 */
	public  Path download(String url) throws MalformedURLException, IOException {
		return download(new URL(url));
	}
	/**
	 * {@link InternetUtils#download(URL, Path, DownloadListener)}
	 */
	public  Path download(URL url) throws IOException {
		return download(url, config, listener);
	}
	public  Path download(URL url, Path savePath, ConnectionConfig config) throws IOException {
		return download(url, config, config == this.config ? listener : new SimpleDownloadListener(config));
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
	public Path download(URL url, ConnectionConfig config, DownloadListener listener) throws IOException {
		URLConnection con = connection(url, config);
		con.connect();

		if(listener.extractNameFromWeb(url)){
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
			listener.nameExtracted(url, name.indexOf('?') < 0 ? name : name.substring(0, name.indexOf('?')));
		}
		
		if(listener.skipDownload(url))
			return null;

		Path temp = tempFile();
		int bsize = config.buffer_size;
		byte[] bytes;
		if(bsize == ConnectionConfig.DEFAULT_BUFFER_SIZE)
			bytes = BUFFERS.poll();
		else
			bytes = new byte[bsize];

		long conFileSize = con.getContentLengthLong();
		listener.progress(0, conFileSize);

		int totalRead = 0;

		try(InputStream is = con.getInputStream();
				OutputStream os = Files.newOutputStream(temp, CREATE_NEW, WRITE, TRUNCATE_EXISTING);
				) {
			int n = 0;

			while((n = is.read(bytes)) != -1) {
				os.write(bytes, 0, n);
				totalRead += n;
				listener.progress(totalRead, conFileSize);
			}
		} finally {
			if(bsize == ConnectionConfig.DEFAULT_BUFFER_SIZE)
				BUFFERS.offer(bytes);
		}
		
		listener.compleated(url, totalRead);
		return temp;
	}
	
	private static final String cm_name = InternetUtils.class.getName()+"-download-";
	
	private Path tempFile() {
		Path temp = MyUtilsPath.TEMP_DIR;
		Path p = temp.resolve(cm_name + COUNTER.incrementAndGet());
		
		while(Files.exists(p))
			p = temp.resolve(cm_name + COUNTER.incrementAndGet());
		
		return p;
	}
	public static URLConnection connection(String url) throws IOException {
		return connection(new URL(url));
	}
	public static URLConnection connection(URL url) throws IOException {
		return connection(url, _DEFAULT_CONFIG);
	}
	public static URLConnection connection(String url, ConnectionConfig config) throws IOException {
		return connection(new URL(url), config);
	}
	/**
	 * create a connection<br>
	 * does'nt call connection.connect();
	 * @param url
	 * @param config
	 * @return
	 * @throws IOException
	 */
	public static URLConnection connection(URL url, ConnectionConfig config) throws IOException {
		URLConnection con = url.openConnection();
		con.setRequestProperty("User-Agent", config.user_agent);
		con.setConnectTimeout(config.connect_timeout);
		con.setReadTimeout(config.read_timeout);
		
		return con;
	}
}
