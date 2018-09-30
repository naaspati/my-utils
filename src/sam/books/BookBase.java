package sam.books;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static sam.books.BooksMeta.*;

public class BookBase {
    private final int book_id ;
    private final String name ;
    private final String file_name ;
    private final int path_id ;
    private final String parentFolderSubpath; 
    private final String author ;
    private final String isbn ;
    private final String page_count ;
    private final String year ;
    private final String description ;
    private final BookStatus status;
    
    public BookBase(ResultSet rs, String parentFolderSubpath) throws SQLException {
    	this.book_id = rs.getInt(BOOK_ID );
        this.name = rs.getString(NAME );
        this.file_name = rs.getString(FILE_NAME );
        this.path_id = rs.getInt(PATH_ID );
        this.parentFolderSubpath = parentFolderSubpath;
        this.author = rs.getString(AUTHOR );
        this.isbn = rs.getString(ISBN );
        this.page_count = rs.getString(PAGE_COUNT );
        this.year = rs.getString(YEAR );
        this.description = rs.getString(DESCRIPTION);
        this.status = Optional.ofNullable(rs.getString(STATUS)).map(BookStatus::valueOf).orElse(BookStatus.NONE);
	}
    
    public BookBase(
            int book_id ,
            String name ,
            String file_name ,
            int path_id ,
            String parentFolderSubpath, 
            String author ,
            String isbn ,
            String page_count ,
            String year ,
            String description,
            BookStatus status
            ) {
        this.book_id = book_id;
        this.name = name;
        this.file_name = file_name;
        this.path_id = path_id;
        this.parentFolderSubpath = parentFolderSubpath;
        this.author = author;
        this.isbn = isbn;
        this.page_count = page_count;
        this.year = year;
        this.description = description;
        this.status = status;
    }
    public Path getExpectedSubpath() {
        return Paths.get(parentFolderSubpath, file_name);
    }
    public Path getExpepectedFullPath() {
    	return BooksDB.ROOT.resolve(parentFolderSubpath).resolve(file_name);
    }
    public Path getFullPath() {
        Path p2 = BooksDB.findBook(getExpepectedFullPath());
        return p2 != null ? p2 : getExpepectedFullPath();
    }

	public int getBookId() { return book_id; }
	public String getName() { return name; }
	public String getFileName() { return file_name; }
	public int getPath_id() { return path_id; }
	public String getParentFolderSubpath() { return parentFolderSubpath; }
	public String getAuthor() { return author; }
	public String getIsbn() { return isbn; }
	public String getPageCount() { return page_count; }
	public String getYear() { return year; }
	public String getDescription() { return description; }
	public BookStatus getStatus() { return status; }
}
