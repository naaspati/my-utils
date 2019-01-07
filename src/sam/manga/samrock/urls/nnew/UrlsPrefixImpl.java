package sam.manga.samrock.urls.nnew;

import static sam.manga.samrock.urls.nnew.UrlsMeta.COLUMN_NAME;
import static sam.manga.samrock.urls.nnew.UrlsMeta.PREFIX;
import static sam.manga.samrock.urls.nnew.UrlsMeta.PROTOCOL;
import static sam.manga.samrock.urls.nnew.UrlsMeta.URLSPREFIX_TABLE_NAME;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import sam.myutils.Checker;
import sam.sql.JDBCHelper;
import sam.sql.sqlite.SQLiteDB;


public final class UrlsPrefixImpl {
	final String column_name;
	final String prefix;
	final String protocol;
	private final String combined;

	UrlsPrefixImpl(ResultSet rs) throws SQLException {
		this.column_name = rs.getString(COLUMN_NAME);
		this.prefix = rs.getString(PREFIX);
		this.protocol = rs.getString(PROTOCOL);
		this.combined = protocol.concat(":").concat(prefix).concat(prefix.charAt(prefix.length() - 1) == '/' ? "" : "/");
	}
	UrlsPrefixImpl(String url, String column_name) throws MalformedURLException{
		Checker.mustBeTrue(Checker.isEmptyTrimmed(column_name), () -> "bad value for column_name:\""+column_name+"\"");
		
		url = url.replace('\\', '/');
		this.column_name = column_name;

		if(url.startsWith("http:"))
			this.protocol = "http";
		else if(url.startsWith("https:"))
			this.protocol = "https";
		else
			throw new IllegalArgumentException("unsupported protocol: "+new URL(url).getProtocol());

		if(url.charAt(url.length() - 1) != '/')
			url = url.concat("/");

		this.prefix = url.substring(url.indexOf(':')+1);
		this.combined = url;  
	}
	
	public String resolve(String suffix) {
		if(suffix == null)
			return null;
		if(suffix.isEmpty())
			return combined;
		return  combined.concat(suffix.charAt(0) == '/' ? suffix.substring(1) : suffix);
	}

	public String getColumnName(){ return this.column_name; }
	public String getPrefix(){ return this.prefix; }
	public String getProtocol(){ return this.protocol; }

	/**
	 * @param db
	 * @return Map(column_name, UrlsPrefixImpl)
	 * @throws SQLException
	 */
	static  Map<String, UrlsPrefixImpl> getAll(SQLiteDB db) throws SQLException{
		return db.collectToMap("SELECT * FROM ".concat(URLSPREFIX_TABLE_NAME), rs -> rs.getString(COLUMN_NAME), UrlsPrefixImpl::new);
	}
	static int insert(Iterable<UrlsPrefixImpl> data, SQLiteDB db)  throws SQLException {
		Objects.requireNonNull(data);
		
		Iterator<UrlsPrefixImpl> itr = data.iterator();
		if(!itr.hasNext())
			return 0;
		
		try(PreparedStatement p = db.prepareStatement(JDBCHelper.insertSQL(URLSPREFIX_TABLE_NAME, COLUMN_NAME, PREFIX, PROTOCOL))) {
			while (itr.hasNext()) {
				UrlsPrefixImpl u = itr.next();
				p.setString(1,u.column_name);
				p.setString(2,u.prefix);
				p.setString(3,u.protocol);
				p.addBatch();
			} 
			return p.executeBatch().length;
		}
	} 
}
