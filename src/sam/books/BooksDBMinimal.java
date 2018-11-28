package sam.books;

import static sam.books.BookStatus.NONE;
import static sam.books.BookStatus.READ;
import static sam.books.BookStatus.SKIPPED;
import static sam.books.BookStatus.valueOf;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import sam.config.MyConfig;
import sam.sql.sqlite.SQLiteDB;

public class BooksDBMinimal extends SQLiteDB {
	
	public static final Path ROOT;
	public static final Path APP_FOLDER;
	public static final Path BACKUP_FOLDER;
	public static final Path DB_PATH;

	static {
		ROOT = Paths.get(MyConfig.BOOKLIST_ROOT).normalize();
		APP_FOLDER = Paths.get(MyConfig.BOOKLIST_APP_DIR).normalize();
		DB_PATH = Paths.get(MyConfig.BOOKLIST_DB).normalize();
		BACKUP_FOLDER = APP_FOLDER.resolve("backups").normalize();
	}
	
	public BooksDBMinimal() throws  SQLException {
		this(DB_PATH);
	}
	public BooksDBMinimal(Path dbpath) throws  SQLException {
		super(dbpath);
	}
	public static Path findBook(Path expectedPath) {
		if(Files.exists(expectedPath))
			return expectedPath;
		Path path = expectedPath.resolveSibling("_read_").resolve(expectedPath.getFileName());

		if(Files.exists(path))
			return path;

		File[] dirs = expectedPath.getParent().toFile().listFiles(f -> f.isDirectory());

		if(dirs == null || dirs.length == 0)
			return null;

		String name = expectedPath.getFileName().toString();

		for (File file : dirs) {
			File f = new File(file, name);
			if(f.exists())
				return f.toPath();
		}
		return null;
	}
	public static BookStatus getStatusFromDir(Path dir) {
		Path name = dir.getFileName();
		if(name.equals(READ.getPathName()))
			return READ;
		if(name.equals(SKIPPED.getPathName()))
			return SKIPPED;

		String s = name.toString();
		if(s.charAt(0) == '_' && s.charAt(s.length() - 1) == '_')
			return valueOf(s.substring(1, s.length() - 1).toUpperCase());

		return NONE;
	}
	public static BookStatus getStatusFromFile(Path p) {
		return getStatusFromDir(p.getParent()); 
	} 

}
