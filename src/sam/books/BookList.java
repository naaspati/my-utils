package sam.books;

import static sam.sql.querymaker.QueryMaker.qm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sam.collection.CollectionUtils;
import sam.collection.Iterable2;
import sam.collection.Iterables;
import sam.sql.querymaker.Select;

public abstract class  BookList<B extends BookImpl> extends ArrayList<B> {
    private static final long serialVersionUID = 1L;

    private static final List<Integer> SELECT_ALL = new ArrayList<>();  

    public BookList(Path bookDbPath) throws  SQLException {
        this(bookDbPath, SELECT_ALL);
    }
    public BookList() throws  SQLException {
        this(SELECT_ALL);
    }
    public BookList(Iterable<Integer> bookIdsToLoad) throws  SQLException {
        this(BooksDB.DB, bookIdsToLoad);
    }
    public BookList(Path bookDbPath, Iterable<Integer> bookIdsToLoad) throws  SQLException {
        Objects.requireNonNull(bookIdsToLoad);

        if(Files.notExists(bookDbPath))
            throw new IllegalArgumentException("bookdb not found"+bookDbPath );

        Iterable2<Integer> iter = Iterables.wrap(bookIdsToLoad);
        
        if(bookIdsToLoad != SELECT_ALL && !iter.hasNext())
            return;

        Select qm = qm().selectAllFrom(BooksMeta.TABLE_NAME);
        if(bookIdsToLoad != SELECT_ALL)
            qm = qm.where(w -> w.in(BooksMeta.BOOK_ID, iter, false));
        
        String bookSql = qm.build();

        try(BooksDB db = new BooksDB(bookDbPath)) {
            List<PathsImpl> list = PathsImpl.getAll(db);
            Map<Integer, PathsImpl> paths = CollectionUtils.map(list, PathsImpl::getPathId, t -> t);
            db.iterate(bookSql, rs -> add( newBook(rs, paths.get(rs.getInt(BooksMeta.PATH_ID ))) ));
        }
    }
    protected abstract B newBook(ResultSet rs, PathsImpl dirPath) throws SQLException;
}
