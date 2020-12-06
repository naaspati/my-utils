package sam.sql.sqlite;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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

import sam.functions.BiConsumerWithException;
import sam.functions.ConsumerWithException;
import sam.functions.FunctionWithException;
import sam.functions.TriConsumerWithException;

public class Sqlite4JavaDB implements AutoCloseable {
	public static final TriConsumerWithException<SQLiteStatement, String, Integer, SQLiteException> STRING_BINDER = (st, value, index) -> st.bind(index, value); 
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	public SQLiteConnection con;

	public Sqlite4JavaDB(Path file, boolean allowCreate) throws SQLiteException {
		this(file.toFile(), allowCreate);
	}

	public Sqlite4JavaDB(String file, boolean allowCreate) throws SQLiteException {
		this(new File(file), allowCreate);
	}

	public Sqlite4JavaDB(File file, boolean allowCreate) throws SQLiteException {
		this.con = new SQLiteConnection(file);
		this.con.open(allowCreate);
	}
	
	public boolean createMetaTable(String... keys) throws SQLiteException {
	    for (String s : keys) 
	        Objects.requireNonNull(s);
	    
	    if(getFirst("SELECT COUNT(*) FROM sqlite_master WHERE name='sam_db_meta';", null, getInt(0)) == 0) {
	        con.exec("CREATE TABLE sam_db_meta (key" + 
	                "    key   TEXT NOT NULL UNIQUE," + 
	                "    value TEXT," + 
	                "    PRIMARY KEY(key)" + 
	                ");");
	        
	        if(keys.length != 0) {
	            for (String k : keys) 
	                insertMeta(k, null);
	        }
	        return true;
	    }
	    return false;
	}
	
	public void insertMeta(String key, String value) throws SQLiteException {
	    Objects.requireNonNull(key);
	    execute("INSERT INTO sam_db_meta(key, value) VALUES(?,?);", st -> {
	        st.bind(1, key);
	        st.bind(2, value);
	    });
    }
	
	public void updateMeta(String key, String value) throws SQLiteException {
	    Objects.requireNonNull(key);
        execute("UPDATE sam_db_meta SET value=? WHERE key=?;", st -> {
            st.bind(1, key);
            st.bind(2, value);
        });
    }
	
	public String getMeta(String key) throws SQLiteException {
	    Objects.requireNonNull(key);
	    return getFirst("SELECT value FROM sam_db_meta WHERE key=?;", st -> st.bind(1, key), s -> s.columnString(0));
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

	public void rollback() throws SQLiteException {
		con.exec("ROLLBACK; ");
	}

	public int exec(String sql) throws SQLiteException {
		con.exec(sql);
		return con.getChanges();
	}

	public int execute(String sql, ConsumerWithException<SQLiteStatement, SQLiteException> binder)
			throws SQLiteException {
		LOGGER.debug("execute: {}", sql);
		SQLiteStatement st = con.prepare(sql, false);
		try {
			if (binder != null)
				binder.accept(st);
			st.step();
			return con.getChanges();
		} finally {
			st.dispose();
		}
	}

	public <E> E getFirstByInt(String sql, int value, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper)
			throws SQLiteException {
		return getFirst(sql, st -> st.bind(1, value), mapper);
	}

	/**
	 * <pre>
	 * try (SQLiteStatement rs = statement.executeQuery(sql)) {
	 * 	return action.accept(rs);
	 * }
	 * </pre>
	 * 
	 * @param sql
	 * @param stmnt  can be null, if null new statement is created
	 * @param action
	 * @throws SQLiteException
	 */
	public <E> E getFirst(String sql, ConsumerWithException<SQLiteStatement, SQLiteException> binder,
			FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
		SQLiteStatement s = prepare(sql, binder != null, binder);
		try {
			if (s.step())
				return mapper.apply(s);
		} finally {
			s.dispose();
		}
		return null;
	}

	public void iterate(String sql, ConsumerWithException<SQLiteStatement, SQLiteException> action)
			throws SQLiteException {
		iterate(prepare(sql), action);
	}

	public static void iterate(SQLiteStatement rs, ConsumerWithException<SQLiteStatement, SQLiteException> action)
			throws SQLiteException {
		try {
			while (rs.step())
				action.accept(rs);
		} finally {
			rs.dispose();
		}
	}

	public void iterateStoppable(String sql, FunctionWithException<SQLiteStatement, Boolean, SQLiteException> action)
			throws SQLiteException {
		iterateStoppable(prepare(sql), action);
	}

	public static void iterateStoppable(SQLiteStatement rs,
			FunctionWithException<SQLiteStatement, Boolean, SQLiteException> action) throws SQLiteException {
		try {
			while (rs.step()) {
				if (!action.apply(rs))
					break;
			}
		} finally {
			rs.dispose();
		}
	}

	public <C extends Collection<E>, E> C collect(String sql, C sink,
			FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
		return collect(prepare(sql), sink, mapper);
	}

	public <E> ArrayList<E> collectToList(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper)
			throws SQLiteException {
		return collect(prepare(sql), new ArrayList<>(), mapper);
	}

	public static <E> ArrayList<E> collectToList(SQLiteStatement rs,
			FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
		return collect(rs, new ArrayList<>(), mapper);
	}

	public static <C extends Collection<E>, E> C collect(SQLiteStatement rs, C sink,
			FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
		try {
			while (rs.step())
				sink.add(mapper.apply(rs));
		} finally {
			rs.dispose();
		}
		return sink;
	}

	public <K, V> HashMap<K, V> collectToMap(String sql,
			FunctionWithException<SQLiteStatement, K, SQLiteException> keymapper,
			FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
		return collect(prepare(sql), new HashMap<>(), keymapper, valuemapper);
	}

	public static <K, V> HashMap<K, V> collectToMap(SQLiteStatement rs,
			FunctionWithException<SQLiteStatement, K, SQLiteException> keymapper,
			FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
		return collect(rs, new HashMap<>(), keymapper, valuemapper);
	}

	public <M extends Map<K, V>, K, V> M collect(String sql, M sink,
			FunctionWithException<SQLiteStatement, K, SQLiteException> keymapper,
			FunctionWithException<SQLiteStatement, V, SQLiteException> valuemapper) throws SQLiteException {
		return collect(prepare(sql), sink, keymapper, valuemapper);
	}

	public static <M extends Map<K, V>, K, V> M collect(SQLiteStatement rs, M sink,
			FunctionWithException<SQLiteStatement, K, SQLiteException> keymapper,
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

	public <E> Stream<E> stream(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper,
			Consumer<SQLiteException> onError) throws SQLiteException {
		return stream(prepare(sql), mapper, onError);
	}

	/**
	 * will throw runtime exception onError
	 * 
	 * @param sql
	 * @return
	 * @throws SQLiteException
	 */
	public static <E> Stream<E> stream(SQLiteStatement rs,
			FunctionWithException<SQLiteStatement, E, SQLiteException> mapper, Consumer<SQLiteException> onError)
			throws SQLiteException {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(rs, mapper, onError),
				Spliterator.ORDERED | Spliterator.IMMUTABLE), false).onClose(() -> rs.dispose());
	}

	public <E> Iterator<E> iterator(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper,
			Consumer<SQLiteException> onError) throws SQLiteException {
		return iterator(prepare(sql), mapper, onError);
	}

	public SQLiteStatement prepare(String sql) throws SQLiteException {
		return prepare(sql, false, null);
	}

	public SQLiteStatement prepare(String sql, boolean cached) throws SQLiteException {
		return prepare(sql, cached, null);
	}

	public SQLiteStatement prepare(String sql, boolean cached,
			ConsumerWithException<SQLiteStatement, SQLiteException> binder) throws SQLiteException {
		LOGGER.debug(sql);
		SQLiteStatement st = con.prepare(sql, cached);
		if (binder != null)
			binder.accept(st);
		return st;
	}

	public static <E> Iterator<E> iterator(SQLiteStatement rs,
			FunctionWithException<SQLiteStatement, E, SQLiteException> mapper, Consumer<SQLiteException> onError)
			throws SQLiteException {
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

	public <E> Stream<E> stream(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper)
			throws SQLiteException {
		return stream(sql, mapper, DEFAULT_ON_ERROR);
	}

	public static <E> Stream<E> stream(SQLiteStatement rs,
			FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
		return stream(rs, mapper, DEFAULT_ON_ERROR);
	}

	public <E> Iterator<E> iterator(String sql, FunctionWithException<SQLiteStatement, E, SQLiteException> mapper)
			throws SQLiteException {
		return iterator(sql, mapper, DEFAULT_ON_ERROR);
	}

	public static <E> Iterator<E> iterator(SQLiteStatement rs,
			FunctionWithException<SQLiteStatement, E, SQLiteException> mapper) throws SQLiteException {
		return iterator(rs, mapper, DEFAULT_ON_ERROR);
	}

	public static final Consumer<SQLiteException> DEFAULT_ON_ERROR = e -> {
		throw new RuntimeException(e);
	};
	
	public <E> SQLiteStatement bindAll(String sql, Iterable<E> data, TriConsumerWithException<SQLiteStatement, E, Integer, SQLiteException> binder) throws SQLiteException {
		return bindAll(prepare(sql, false), data, binder);
	}
	
	public <E> SQLiteStatement bindAll(String sql, E[] data, TriConsumerWithException<SQLiteStatement, E, Integer, SQLiteException> binder) throws SQLiteException {
		return bindAll(prepare(sql, false), data, binder);
	}
	
	public <E> SQLiteStatement bindAll(String sql, int[] data) throws SQLiteException {
		return bindAll(prepare(sql, false), data);
	}
	
	public static <E> SQLiteStatement bindAll(SQLiteStatement st, Iterable<E> data, TriConsumerWithException<SQLiteStatement, E, Integer, SQLiteException> binder) throws SQLiteException {
		int n = 1;
		for (E e : data) 
			binder.accept(st, e, n++);
		return st;
	}
	
	public static <E> SQLiteStatement bindAll(SQLiteStatement st, E[] data, TriConsumerWithException<SQLiteStatement, E, Integer, SQLiteException> binder) throws SQLiteException {
		int n = 1;
		for (E e : data) 
			binder.accept(st, e, n++);
		return st;
	}
	
	public static <E> SQLiteStatement bindAll(SQLiteStatement st, int[] data) throws SQLiteException {
		int n = 1;
		for (int e : data) 
			st.bind(n++, e);
		return st;
	}

	public <E> int batch(String sql, Iterable<E> data,
			BiConsumerWithException<SQLiteStatement, E, SQLiteException> binder) throws SQLiteException {
		Iterator<E> itr = data.iterator();
		if (!itr.hasNext())
			return 0;
		
		LOGGER.debug("BATCH: {}", sql);
		SQLiteStatement st = con.prepare(sql);
		boolean started = false;
		try {
			this.startTransaction();
			started = true;
			int n = 0;
			while (itr.hasNext()) {
				binder.accept(st, itr.next());
				st.step();
				st.reset(false);
				n++;
			}
			this.commit();
			return n;
		} catch (SQLiteException e) {
			if (started)
				this.rollback();
			throw e;
		} finally {
			st.dispose();
		}
	}

	public static FunctionWithException<SQLiteStatement, String, SQLiteException>  getString(int index) {
		return rs -> rs.columnString(index);
	}
	public static FunctionWithException<SQLiteStatement, Integer, SQLiteException>  getInt(int index) {
		return rs -> rs.columnInt(index);
	}
}
