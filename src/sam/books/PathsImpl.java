package sam.books;
import static sam.books.PathsMeta.MARKER;
import static sam.books.PathsMeta.PATH;
import static sam.books.PathsMeta.PATH_ID;
import static sam.books.PathsMeta.TABLE_NAME;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PathsImpl {
	private final int path_id;
	private final String path;
	private final String marker;

	public PathsImpl(ResultSet rs) throws SQLException {
		this.path_id = rs.getInt(PATH_ID);
		this.path = rs.getString(PATH);
		this.marker = rs.getString(MARKER);
	}
	

	public PathsImpl(int path_id, String path, String marker) {
		this.path_id = path_id;
		this.path = path;
		this.marker = marker;
	}
	
	public int getPathId(){ return this.path_id; }
	public String getPath(){ return this.path; }
	public String getMarker(){ return this.marker; }

	private Path fullpath;
	public Path getFullPath() {
		return fullpath != null ? fullpath : (fullpath = BooksDBMinimal.ROOT.resolve(path));
	}
	
	
	public static List<PathsImpl> getAll(BooksDBMinimal db) throws SQLException{
		return db.collectToList("SELECT * FROM "+TABLE_NAME, PathsImpl::new);
	}
	
	public static final String FIND_BY_PATH_ID = "SELECT * FROM "+TABLE_NAME+" WHERE "+PATH_ID+"=";
	public static PathsImpl getByPathId(BooksDBMinimal db, int path_id) throws SQLException {
		return db.findFirst(FIND_BY_PATH_ID+path_id, PathsImpl::new);
	}
}

