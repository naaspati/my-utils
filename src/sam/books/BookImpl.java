package sam.books;
import static sam.books.BooksMeta.AUTHOR;
import static sam.books.BooksMeta.BOOK_ID;
import static sam.books.BooksMeta.DESCRIPTION;
import static sam.books.BooksMeta.FILE_NAME;
import static sam.books.BooksMeta.ISBN;
import static sam.books.BooksMeta.NAME;
import static sam.books.BooksMeta.PAGE_COUNT;
import static sam.books.BooksMeta.PATH_ID;
import static sam.books.BooksMeta.STATUS;
import static sam.books.BooksMeta.TABLE_NAME;
import static sam.books.BooksMeta.URL;
import static sam.books.BooksMeta.YEAR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookImpl {
	private final int book_id ;
	private final String name ;
	private final String file_name ;
	private final int path_id ;
	private final PathsImpl dir; 
	private final String author ;
	private final String isbn ;
	private final int page_count ;
	private final String year ;
	private final String description ;
	private final BookStatus status;
	private final String url;

	public BookImpl(ResultSet rs, PathsImpl dir) throws SQLException {
		this.book_id = rs.getInt(BOOK_ID);
		this.name = rs.getString(NAME);
		this.file_name = rs.getString(FILE_NAME);
		this.path_id = rs.getInt(PATH_ID);
		this.author = rs.getString(AUTHOR);
		this.isbn = rs.getString(ISBN);
		this.page_count = rs.getInt(PAGE_COUNT);
		this.year = rs.getString(YEAR);
		this.description = rs.getString(DESCRIPTION);
		this.status = Optional.ofNullable(rs.getString(STATUS)).map(BookStatus::valueOf).orElse(BookStatus.NONE);
		this.url = rs.getString(URL);

		this.dir = dir;

	}

	BookImpl(int book_id, String name, String file_name, int path_id, PathsImpl dir, String author,
			String isbn, int page_count, String year, String description, BookStatus status, String url) {
		this.book_id = book_id;
		this.name = name;
		this.file_name = file_name;
		this.path_id = path_id;
		this.dir = dir;
		this.author = author;
		this.isbn = isbn;
		this.page_count = page_count;
		this.year = year;
		this.description = description;
		this.status = status;
		this.url = url;
	}

	public static BookBuilder builder() { return new BookBuilder(); };

	public int getBookId(){ return this.book_id; }
	public String getName(){ return this.name; }
	public String getFileName(){ return this.file_name; }
	public int getPathId(){ return this.path_id; }
	public String getAuthor(){ return this.author; }
	public String getIsbn(){ return this.isbn; }
	public int getPageCount(){ return this.page_count; }
	public String getYear(){ return this.year; }
	public String getDescription(){ return this.description; }
	public BookStatus getStatus(){ return this.status; }
	public String getUrl(){ return this.url; }

	public PathsImpl getDir() { return dir; }

	public Path getExpectedSubpath() {
		return Paths.get(dir.getPath(), file_name);
	}
	public Path getExpepectedFullPath() {
		return dir.getFullPath().resolve(file_name);
	}
	public Path getFullPath() {
		Path p2 = BooksDB.findBook(getExpepectedFullPath());
		return p2 != null ? p2 : getExpepectedFullPath();
	}



	public static List<BookImpl> getAll(BooksDB db) throws SQLException{
		Map<Integer, PathsImpl> paths = PathsImpl.getAll(db).stream().collect(Collectors.toMap(PathsImpl::getPathId, s -> s));
		return db.collectToList("SELECT * FROM "+TABLE_NAME, rs -> new BookImpl(rs, paths.get(rs.getInt(PATH_ID))));
	}
	public static final String FIND_BY_ID = "SELECT * FROM "+TABLE_NAME+ " NATURAL JOIN "+PathsMeta.TABLE_NAME+" WHERE "+BOOK_ID+"=";
	public static BookImpl getById(BooksDB db, int id) throws SQLException {
		return db.findFirst(FIND_BY_ID+id, rs -> new BookImpl(rs, new PathsImpl(rs)));
	}

}

