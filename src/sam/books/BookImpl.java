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
import static sam.books.BooksMeta.*;
import static sam.books.BooksMeta.URL;
import static sam.books.BooksMeta.YEAR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BookImpl {
	public final int book_id ;
	public final String name ;
	public final String file_name ;
	public final int path_id ;
	public final String author ;
	public final String isbn ;
	public final int page_count ;
	public final String year ;
	public final String description ;
	public final BookStatus status;
	public final String url;
	
	public final PathsImpl dir;

	public BookImpl(ResultSet rs) throws SQLException {
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

		this.dir = new PathsImpl(rs);
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

	@Override
	public String toString() {
		return "BookImpl [book_id=" + book_id + ", name=" + name + ", file_name=" + file_name + ", path_id=" + path_id
				+ ", author=" + author + ", isbn=" + isbn + ", page_count=" + page_count + ", year=" + year
				+ ", description=" + description + ", status=" + status + ", url=" + url + ", dir=" + dir + "]";
	}



	public static final String SELECT_ALL  = "SELECT * FROM "+BOOK_TABLE_NAME+ " NATURAL JOIN "+PATH_TABLE_NAME; 

	public static List<BookImpl> getAll(BooksDB db) throws SQLException{
		return db.collectToList(SELECT_ALL, BookImpl::new);
	}
	public static final String FIND_BY_ID = SELECT_ALL+" WHERE "+BOOK_ID+"=";
	public static BookImpl getById(BooksDB db, int id) throws SQLException {
		return db.findFirst(FIND_BY_ID+id, BookImpl::new);
	}

}

