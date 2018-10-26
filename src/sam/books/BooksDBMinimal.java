package sam.books;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import sam.config.MyConfig;
import sam.sql.sqlite.SQLiteDB;

public class BooksDBMinimal extends SQLiteDB {
	
	public static final Path ROOT;
	public static final Path APP_FOLDER;
	public static final Path BACKUP_FOLDER;
	public static final Path DB;

	static {
		ROOT = Paths.get(MyConfig.BOOKLIST_ROOT).normalize();
		APP_FOLDER = Paths.get(MyConfig.BOOKLIST_APP_DIR).normalize();
		DB = Paths.get(MyConfig.BOOKLIST_DB).normalize();
		BACKUP_FOLDER = APP_FOLDER.resolve("backups").normalize();
	}
	
	public BooksDBMinimal() throws  SQLException {
		this(DB);
	}
	public BooksDBMinimal(Path dbpath) throws  SQLException {
		super(dbpath);
	}

}
