package sam.internetutils;

import java.io.IOException;

import sam.config.Properties2;
import sam.io.IOConstants;

public class ConnectionConfig {
	public  static final int DEFAULT_CONNECT_TIMEOUT;
	public  static final int DEFAULT_READ_TIMEOUT;
	public  static final String DEFAULT_USER_AGENT;
	public  static final int DEFAULT_BUFFER_SIZE;
	
	
	public final int connect_timeout; 
	public final int read_timeout;
	public final boolean show_download_warnings;
	public final boolean show_warnings;
	public final int buffer_size;
	public final String user_agent;

	static {
		Properties2 config = config0();

		config.putIfAbsent("DEFAULT_CONNECT_TIMEOUT", String.valueOf(15*1000));
		config.putIfAbsent("DEFAULT_READ_TIMEOUT", String.valueOf(60*1000));
		config.putIfAbsent("DEFAULT_BUFFER_SIZE", String.valueOf(IOConstants.defaultBufferSize()));
		config.putIfAbsent("DEFAULT_USER_AGENT", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");

		DEFAULT_CONNECT_TIMEOUT = Integer.parseInt(config.get("DEFAULT_CONNECT_TIMEOUT"));
		DEFAULT_READ_TIMEOUT = Integer.parseInt(config.get("DEFAULT_READ_TIMEOUT"));
		DEFAULT_USER_AGENT = config.get("DEFAULT_USER_AGENT");
		DEFAULT_BUFFER_SIZE = Integer.parseInt(config.get("DEFAULT_BUFFER_SIZE"));
	}
	
	public ConnectionConfig() {
		connect_timeout = DEFAULT_CONNECT_TIMEOUT; 
		read_timeout = DEFAULT_READ_TIMEOUT;
		buffer_size = DEFAULT_BUFFER_SIZE;
		user_agent = DEFAULT_USER_AGENT;
		show_download_warnings = false;
		show_warnings = false;
	}
	public ConnectionConfig(int connect_timeout, int read_timeout, boolean show_download_warnings,
			boolean skip_download_if_exists, boolean show_warnings, int buffer_size, String user_agent) {
		
		this.connect_timeout = connect_timeout;
		this.read_timeout = read_timeout;
		this.show_download_warnings = show_download_warnings;
		this.show_warnings = show_warnings;
		this.buffer_size = buffer_size;
		this.user_agent = user_agent == null ? DEFAULT_USER_AGENT : user_agent;
	}
	
	private static Properties2 config0() {
		try {
			Properties2 p = new Properties2(InternetUtils.class.getResourceAsStream("1509532146836-internet-utils.properties"));
			p.setSystemLookup(true, true);
			return p;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public String toString() {
		return "ConnectionConfig [connect_timeout=" + connect_timeout + ", read_timeout=" + read_timeout
				+ ", show_download_warnings=" + show_download_warnings + ", show_warnings=" + show_warnings + ", buffer_size=" + buffer_size
				+ ", user_agent=" + user_agent + "]";
	}
}
