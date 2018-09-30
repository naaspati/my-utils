package sam.books;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sam.sql.querymaker.QueryMaker;
import sam.sql.querymaker.Select;

public abstract class  BookList<B extends BookBase> extends ArrayList<B> {
    private static final long serialVersionUID = 1L;

    private static final List<Integer> SELECT_ALL = new ArrayList<>();  

    public BookList(Path bookDbPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this(bookDbPath, SELECT_ALL);
    }
    public BookList() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this(SELECT_ALL);
    }
    public BookList(Iterable<Integer> bookIdsToLoad) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this(BooksDB.DB, bookIdsToLoad);
    }
    public BookList(Path bookDbPath, Iterable<Integer> bookIdsToLoad) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Objects.requireNonNull(bookIdsToLoad);

        if(Files.notExists(bookDbPath))
            throw new IllegalArgumentException("bookdb not found"+bookDbPath );

        Iterator<Integer> iter = bookIdsToLoad.iterator();
        
        if(bookIdsToLoad != SELECT_ALL && !iter.hasNext())
            return;

        Select qm = QueryMaker.getInstance().selectAllFrom(BooksMeta.TABLE_NAME);
        if(bookIdsToLoad != SELECT_ALL)
            qm = qm.where(w -> w.in(BooksMeta.BOOK_ID, iter, false));
        
        String bookSql = qm.build();

        try(BooksDB db = new BooksDB(bookDbPath)) {
            Map<Integer, String> paths = db.pathsMap();
            db.iterate(bookSql, rs -> add( newBook(rs, paths.get(rs.getInt(BooksMeta.PATH_ID ))) ));
        }
    }
    protected abstract B newBook(ResultSet rs, String dirPath) throws SQLException;
}
