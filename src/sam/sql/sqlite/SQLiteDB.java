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

import sam.myutils.System2;
import sam.sql.JDBCHelper;

public class SQLiteDB extends JDBCHelper {
    private final Object path;

    public SQLiteDB(String dbPath) throws SQLException {
        this(Paths.get(dbPath), new Properties(), false);
    }
    public SQLiteDB(Path dbPath) throws SQLException {
        this(dbPath, new Properties(), false);
    }
    public SQLiteDB(File dbPath) throws SQLException {
        this(dbPath.toPath(), new Properties(), false);
    }
    
    /**
     * 
     * @param dbPath can be a file, path, string, url
     * @param prop
     * @param createDefaultStatement
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public SQLiteDB(Path dbPath, Properties prop, boolean create) throws SQLException {
    	super(connection(dbPath, prop, create));
        
        // JDBC.class.getCanonicalName() =  "org.sqlite.JDBC"
        // JDBC.PREFIX = "jdbc:sqlite:"
       this.path = dbPath; 
    }
    private static Connection connection(Path dbPath, Properties prop, boolean create) throws SQLException {
    	if(create && Files.notExists(dbPath))
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
    public Object getPath() {
        return path;
    }
    
    public long getSequnceValue(String tableName) throws SQLException {
    	return executeQuery("SELECT seq from sqlite_sequence where name='"+tableName+"'", rs -> rs.next() ? rs.getLong(1) : 0);
    } 
}
