package sam.books;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookListDefault extends BookList<BookImpl> {
    private static final long serialVersionUID = 7061921690620663111L;
    
    public BookListDefault() throws  SQLException {
        super();
    }
    public BookListDefault(Iterable<Integer> bookIdsToLoad) throws  SQLException {
        super(bookIdsToLoad);
    }
    public BookListDefault(Path bookDbPath, Iterable<Integer> bookIdsToLoad) throws  SQLException {
        super(bookDbPath, bookIdsToLoad);
    }
    public BookListDefault(Path bookDbPath) throws  SQLException {
        super(bookDbPath);
    }
    @Override
    protected BookImpl newBook(ResultSet rs, PathsImpl parent) throws SQLException {
        return new BookImpl(rs, parent);
    }

}
