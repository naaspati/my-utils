package sam.books;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sam.sql.querymaker.QueryMaker;
import sam.sql.querymaker.Select;
import sam.sql.sqlite.SQLiteManeger;

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
        this(BookUtils.DB, bookIdsToLoad);
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

        try(SQLiteManeger db = new SQLiteManeger(bookDbPath)) {
            Map<Integer, String> paths = BookUtils.pathsMap(db);
            
            db.iterate(bookSql, rs -> add(
                    newBook(
                            rs.getInt(BooksMeta.BOOK_ID ),
                            rs.getString(BooksMeta.NAME ),
                            rs.getString(BooksMeta.FILE_NAME ),
                            rs.getInt(BooksMeta.PATH_ID ),
                            paths.get(rs.getInt(BooksMeta.PATH_ID )),
                            rs.getString(BooksMeta.AUTHOR ),
                            rs.getString(BooksMeta.ISBN ),
                            rs.getString(BooksMeta.PAGE_COUNT ),
                            rs.getString(BooksMeta.YEAR ),
                            rs.getString(BooksMeta.DESCRIPTION)
                            )
                    ));
        }
    }
    protected abstract B newBook(
            int book_id ,
            String name ,
            String file_name ,
            int path_id ,
            String parentFolderSubpath, 
            String author ,
            String isbn ,
            String page_count ,
            String year ,
            String description 
            );
}
