package sam.sql.sqlite;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import sam.myutils.System2;
import sam.sql.JDBCHelper;

public class SQLiteDB extends JDBCHelper {
    private final Path path;
    
    private static Properties defaultProperties() {
		return new Properties();
	}
    public SQLiteDB(String dbPath) throws SQLException {
        this(Paths.get(dbPath), defaultProperties(), false);
    }
    
	public SQLiteDB(Path dbPath, boolean create) throws SQLException {
        this(dbPath, defaultProperties(), create);
    }
    public SQLiteDB(Path dbPath) throws SQLException {
        this(dbPath, defaultProperties(), false);
    }
    public SQLiteDB(File dbPath) throws SQLException {
        this(dbPath.toPath(), defaultProperties(), false);
    }
    
    /**
     * 
     * @param dbPath can be a file, path, string, url
     * @param prop
     * @param create
     * @throws SQLException
     */
    public SQLiteDB(Path dbPath, Properties prop, boolean create) throws SQLException {
    	super(connection(dbPath, prop, create));
    	LoggerFactory.getLogger(getClass()).debug("SQLite Connection open: jdbc:sqlite:"+dbPath.getFileName());
        
        // JDBC.class.getCanonicalName() =  "org.sqlite.JDBC"
        // JDBC.PREFIX = "jdbc:sqlite:"
       this.path = dbPath; 
    }
    
    public SQLiteDB(Connection connection, Path path) {
		super(connection);
		this.path = path;
	}
	private static Connection connection(Path dbPath, Properties prop, boolean create) throws SQLException {
    	if(!create && Files.notExists(dbPath))
    		throw new SQLException(new FileNotFoundException("db file no found: "+dbPath));
    	
    	try {
    		Driver driver = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
            Connection connection = driver.connect("jdbc:sqlite:"+dbPath, prop);
            connection.setAutoCommit(Boolean.valueOf(System2.lookup("sql.autocommit")));
            return connection;
		} catch (InstantiationException| IllegalAccessException| ClassNotFoundException e) {
			throw new SQLException(e);
		}
    }
    public Path getPath() {
        return path;
    }
    
    /**
     * @param tableName
     * @return
     * @throws SQLException
     */
    public int getSequnceValue(String tableName) throws SQLException {
    	return executeQuery("SELECT seq from sqlite_sequence where name='"+tableName+"'", rs -> rs.next() ? rs.getInt(1) : 0);
    } 
}
