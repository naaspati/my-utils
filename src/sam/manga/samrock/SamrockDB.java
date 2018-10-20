package sam.manga.samrock;

import java.sql.SQLException;

import sam.config.MyConfig;
import sam.sql.sqlite.SQLiteDB;

public class SamrockDB extends SQLiteDB implements AutoCloseable {
    public SamrockDB() throws  SQLException {
        this(MyConfig.SAMROCK_DB);
    }
    public SamrockDB(String dbPath) throws  SQLException {
        super(dbPath);
    }
}
