package sam.sql;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import sam.functions.ConsumerWithException;
import sam.functions.FunctionWithException;

public class Sqlite4javaHelper implements AutoCloseable, QueryHelper {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public SQLiteConnection con;
    
    public Sqlite4javaHelper(Path file, boolean allowCreate) throws SQLiteException {
        this(file.toFile(), allowCreate);
    }
    public Sqlite4javaHelper(String file, boolean allowCreate) throws SQLiteException {
        this(new File(file), allowCreate);
    }

    public Sqlite4javaHelper(File file, boolean allowCreate) throws SQLiteException {
        this.con = new SQLiteConnection(file);
        this.con.open(allowCreate); 
    }

    @Override
    public void close() throws SQLiteException {
        con.dispose();
    }
    
    public void startTransaction() throws SQLiteException {
        con.exec("BEGIN TRANSACTION; ");
    }
    public void commit() throws SQLiteException {
        con.exec("COMMIT; ");
    }

    public int execute(String sql, ConsumerWithException<SQLiteStatement, SQLiteException> binder) throws SQLiteException {
        LOGGER.debug("execute: {}", sql);
        SQLiteStatement st = con.prepare(sql);
        try {
            if(binder != null) 
                binder.accept(st);
            st.step();
            return con.getChanges();
        } finally {
            st.dispose();
        }
    }
    
    public <E> E getFirstByInt(String sql, int value, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
        return getFirst(sql, st -> st.bind(1, value), mapper);
    }

    /**
     * <pre>
     * try (SQLiteStatement rs = statement.executeQuery(sql)) {
     *     return action.accept(rs);
     * }
     * </pre>
     * 
     * @param sql
     * @param stmnt  can be null, if null new statement is created
     * @param action
     * @throws SQLiteException
     */
    public <E> E getFirst(String sql, ConsumerWithException<SQLiteStatement, SQLiteException> binder, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
        SQLiteStatement s = prepare(sql);
        if(binder != null) {
            binder.accept(s);
        }
        try {
            while(s.step()) {
                return mapper.apply(s);
            }
        } finally {
            s.dispose();
        }
        return null;
    }

    public void iterate(String sql, ConsumerWithException<SQLiteStatement, SQLiteException> action) throws SQLiteException {
        iterate(prepare(sql), action);
    }

    public static void iterate(SQLiteStatement rs, ConsumerWithException<SQLiteStatement, SQLiteException> action) throws SQLiteException {
        try {
            while (rs.step())
                action.accept(rs);
        } finally {
            rs.dispose();
        }
    }

    public void iterateStoppable(String sql, FunctionWithException<SQLiteStatement, Boolean, SQLiteException> action) throws SQLiteException {
        iterateStoppable(prepare(sql), action);
    }

    public static void iterateStoppable(SQLiteStatement rs, FunctionWithException<SQLiteStatement, Boolean, SQLiteException> action) throws SQLiteException {
        try {
            while (rs.step()){
                if (!action.apply(rs))
                    break;
            }
        } finally {
            rs.dispose();
        }
    }

    public <C extends Collection<E>, E> C collect(String sql, C sink, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper)
            throws SQLiteException {
        return collect(prepare(sql), sink, mapper);
    }

    public <E> ArrayList<E> collectToList(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
        return collect(prepare(sql), new ArrayList<>(), mapper);
    }

    public static <E> ArrayList<E> collectToList(SQLiteStatement rs, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
        return collect(rs, new ArrayList<>(), mapper);
    }

    public static <C extends Collection<E>, E> C collect(SQLiteStatement rs, C sink, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper)
            throws SQLiteException {
        try {
            while (rs.step())
                sink.add(mapper.apply(rs));
        } finally {
            rs.dispose();
        }
        return sink;
    }

    public <K, V> HashMap<K, V> collectToMap(String sql, FunctionWithException<SQLiteStatement, K, SQLiteException> keymapper,
            FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
        return collect(prepare(sql), new HashMap<>(), keymapper, valuemapper);
    }

    public static <K, V> HashMap<K, V> collectToMap(SQLiteStatement rs, FunctionWithException<SQLiteStatement, K, SQLiteException> keymapper,
            FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
        return collect(rs, new HashMap<>(), keymapper, valuemapper);
    }

    public <M extends Map<K, V>, K, V> M collect(String sql, M sink, FunctionWithException<SQLiteStatement, K, SQLiteException> keymapper,
            FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
        return collect(prepare(sql), sink, keymapper, valuemapper);
    }

    public static <M extends Map<K, V>, K, V> M collect(SQLiteStatement rs, M sink, FunctionWithException<SQLiteStatement, K, SQLiteException> keymapper,
            FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
        try {
            while (rs.step())
                sink.put(keymapper.apply(rs), valuemapper.apply(rs));
        } finally {
            rs.dispose();
        }
        return sink;
    }

    public <K, V> HashMap<K, V> collectToMap2(String sql, Function<V, K> keymapper,
            FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
        return collect2(prepare(sql), new HashMap<>(), keymapper, valuemapper);
    }

    public static <K, V> HashMap<K, V> collectToMap2(SQLiteStatement rs, Function<V, K> keymapper,
            FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
        return collect2(rs, new HashMap<>(), keymapper, valuemapper);
    }

    public <M extends Map<K, V>, K, V> M collect2(String sql, M sink, Function<V, K> keymapper,
            FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
        return collect2(prepare(sql), sink, keymapper, valuemapper);
    }

    public static <M extends Map<K, V>, K, V> M collect2(SQLiteStatement rs, M sink, Function<V, K> keymapper,
            FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
        try {
            while (rs.step()) {
                V v = valuemapper.apply(rs);
                sink.put(keymapper.apply(v), v);
            }
        } finally {
            rs.dispose();
        }
        return sink;
    }

    public <E> Stream<E> stream(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper, Consumer<SQLiteException> onError)
            throws SQLiteException {
        return stream(prepare(sql), mapper, onError);
    }

    /**
     * will throw runtime exception onError
     * 
     * @param sql
     * @return
     * @throws SQLiteException
     */
    public static <E> Stream<E> stream(SQLiteStatement rs, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper, Consumer<SQLiteException> onError)
            throws SQLiteException {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(rs, mapper, onError),
                Spliterator.ORDERED | Spliterator.IMMUTABLE), false).onClose(() -> rs.dispose());
    }

    public <E> Iterator<E> iterator(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper, Consumer<SQLiteException> onError)
            throws SQLiteException {
        return iterator(prepare(sql), mapper, onError);
    }

    public SQLiteStatement prepare(String sql) throws SQLiteException {
        LOGGER.debug(sql);
        return con.prepare(sql);
    }

    public static <E> Iterator<E> iterator(SQLiteStatement rs, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper,
            Consumer<SQLiteException> onError) throws SQLiteException {
        return new Iterator<E>() {
            Boolean next = null;
            {
                next = rs.step();
            }

            @Override
            public E next() {
                next = null;
                try {
                    return mapper.apply(rs);
                } catch (SQLiteException e) {
                    onError.accept(e);
                    next = false;
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                if (next == null) {
                    try {
                        next = rs.step();
                    } catch (SQLiteException e) {
                        onError.accept(e);
                        next = false;
                    }
                }
                return next;
            }
        };
    }

    public <E> Stream<E> stream(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
        return stream(sql, mapper, DEFAULT_ON_ERROR);
    }

    public static <E> Stream<E> stream(SQLiteStatement rs, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
        return stream(rs, mapper, DEFAULT_ON_ERROR);
    }

    public <E> Iterator<E> iterator(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
        return iterator(sql, mapper, DEFAULT_ON_ERROR);
    }

    public static <E> Iterator<E> iterator(SQLiteStatement rs, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
        return iterator(rs, mapper, DEFAULT_ON_ERROR);
    }

    public static final Consumer<SQLiteException> DEFAULT_ON_ERROR = e -> {
        throw new RuntimeException(e);
    };

    public static void setString(int index, SQLiteStatement ps, String value) throws SQLiteException {
        if (value == null)
            ps.bindNull(index);
        else
            ps.bind(index, value);
    }
}
