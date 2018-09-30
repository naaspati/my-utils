package sam.books;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookListDefault extends BookList<BookBase> {
    private static final long serialVersionUID = 7061921690620663111L;
    
    public BookListDefault() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super();
    }
    public BookListDefault(Iterable<Integer> bookIdsToLoad) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super(bookIdsToLoad);
    }
    public BookListDefault(Path bookDbPath, Iterable<Integer> bookIdsToLoad) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super(bookDbPath, bookIdsToLoad);
    }
    public BookListDefault(Path bookDbPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super(bookDbPath);
    }
    @Override
    protected BookBase newBook(ResultSet rs, String parentFolderSubpath) throws SQLException {
        return new BookBase(rs, parentFolderSubpath);
    }

}
