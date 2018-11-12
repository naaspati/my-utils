package sam.manga.mangarock;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import sam.config.MyConfig;
import sam.sql.sqlite.SQLiteDB;

public class MangarockDB extends SQLiteDB {
	public MangarockDB() throws SQLException {
		this(dbPath());
	}
	private static Path dbPath() {
		Path p = Paths.get(MyConfig.MANGAROCK_INPUT_DB);
		return Files.exists(p) ? p : Paths.get(MyConfig.MANGAROCK_DB_BACKUP);
	}

	public MangarockDB(Path dbPath) throws SQLException {
		super(dbPath);
	}
	public MangarockDB(String dbPath) throws SQLException {
		super(dbPath);
	}

}
