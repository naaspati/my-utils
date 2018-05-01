package sam.books;

import java.nio.file.Path;
import java.sql.SQLException;

public class BookListDefault extends BookList<BookBase> {
    private static final long serialVersionUID = 7061921690620663111L;
    
    public BookListDefault() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super();
    }
    public BookListDefault(Iterable<Integer> bookIdsToLoad)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super(bookIdsToLoad);
    }

    public BookListDefault(Path bookDbPath, Iterable<Integer> bookIdsToLoad)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super(bookDbPath, bookIdsToLoad);
    }
    public BookListDefault(Path bookDbPath)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super(bookDbPath);
    }

    @Override
    protected BookBase newBook(int book_id, String name, String file_name, int path_id, String parentFolderSubpath,
            String author, String isbn, String page_count, String year, String description) {
        return new BookBase(book_id, name, file_name, path_id, parentFolderSubpath, author, isbn, page_count, year, description);
    }

}
