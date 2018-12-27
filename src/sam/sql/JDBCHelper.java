package sam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import sam.logging.MyLoggerFactory;
import sam.myutils.Checker;

public abstract class JDBCHelper implements AutoCloseable {
	private Statement defaultStatement;
	private final Connection connection;
	private final Logger LOGGER = MyLoggerFactory.logger(getClass());

	protected JDBCHelper(Connection connection) {
		this.connection = connection;
	}
	public Statement getDefaultStatement() throws SQLException {
		if(defaultStatement != null)
			return defaultStatement;
		return defaultStatement = connection.createStatement();
	}

	public void commit() throws SQLException {
		connection.commit();
	}
	@Override
	public void close() throws SQLException {
		if(defaultStatement != null)
			defaultStatement.close();
		connection.close();
	}
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		LOGGER.fine(() -> "PreparedStatement("+sql+")");
		return connection.prepareStatement(sql);
	}
	public int executeUpdate(String sql) throws SQLException {
		LOGGER.fine(() -> "UPDATE: "+sql);
		return getDefaultStatement().executeUpdate(sql); 
	}
	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}
	/**
	 * .executeQuery(sql)
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public ResultSet query(String sql) throws SQLException {
		LOGGER.fine(() -> "QUERY: "+sql);
		return getDefaultStatement().executeQuery(sql);
	}
	public ResultSet executeQuery(String sql) throws SQLException {
		return query(sql);
	}
	/**
	 * <pre>
	 * try(ResultSet rs = statement.executeQuery(sql)) {
	 *      return action.accept(rs);
	 * }
	 * </pre>
	 * 
	 * @param sql
	 * @param stmnt  can be null, if null new statement is created 
	 * @param action
	 * @throws SQLException
	 */
	public <E> E executeQuery(String sql, SqlFunction<ResultSet, E> action) throws SQLException {
		try(ResultSet rs = query(sql)) {
			return action.apply(rs);
		}
	}
	public void iterate(String sql, SqlConsumer<ResultSet> action) throws SQLException {
		iterate(query(sql), action);
	}
	public static void iterate(ResultSet rs, SqlConsumer<ResultSet> action) throws SQLException {
		try(ResultSet rs2 = rs) {
			while(rs.next()) action.accept(rs);
		}
	}
	public void iterateStoppable(String sql, SqlFunction<ResultSet, Boolean> action) throws SQLException {
		iterateStoppable(query(sql), action);
	}
	public static void iterateStoppable(ResultSet rs, SqlFunction<ResultSet, Boolean> action) throws SQLException {
		try(ResultSet rs2 = rs) {
			while(rs.next()) {
				if(!action.apply(rs))
					break;
			}
		}
	}
	public <C extends Collection<E>, E> C collect(String sql,C sink, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return collect(query(sql), sink, mapper); 
	}
	public <E> ArrayList<E> collectToList(String sql, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return collect(query(sql), new ArrayList<>(), mapper); 
	}
	public static <E> ArrayList<E> collectToList(ResultSet rs, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return collect(rs, new ArrayList<>(), mapper); 
	}
	public static <C extends Collection<E>, E> C collect(ResultSet rs0,C sink, SqlFunction<ResultSet, E> mapper) throws SQLException {
		try(ResultSet rs = rs0) {
			while(rs.next()) sink.add(mapper.apply(rs));
		}
		return sink;
	}

	public <K, V>  HashMap<K, V> collectToMap(String sql, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect(query(sql), new HashMap<>(), keymapper, valuemapper);
	}
	public static <K, V>  HashMap<K, V> collectToMap(ResultSet rs, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect(rs, new HashMap<>(), keymapper, valuemapper);
	}

	public <M extends Map<K, V>, K, V>  M collect(String sql,M sink, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect(query(sql), sink, keymapper, valuemapper);
	}
	public static <M extends Map<K, V>, K, V>  M collect(ResultSet rs0,M sink, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		try(ResultSet rs = rs0) {
			while(rs.next()) sink.put(keymapper.apply(rs), valuemapper.apply(rs));
		}
		return sink;
	}

	public <K, V>  HashMap<K, V> collectToMap2(String sql, Function<V, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect2(query(sql), new HashMap<>(), keymapper, valuemapper);
	}
	public static <K, V>  HashMap<K, V> collectToMap2(ResultSet rs, Function<V, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect2(rs, new HashMap<>(), keymapper, valuemapper);
	}

	public <M extends Map<K, V>, K, V>  M collect2(String sql,M sink, Function<V, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect2(query(sql), sink, keymapper, valuemapper);
	}
	public static <M extends Map<K, V>, K, V>  M collect2(ResultSet rs0,M sink, Function<V, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		try(ResultSet rs = rs0) {
			while(rs.next()) {
				V v = valuemapper.apply(rs);
				sink.put(keymapper.apply(v), v);
			}
		}
		return sink;
	}


	
	public <E> Stream<E> stream(String sql, SqlFunction<ResultSet, E> mapper, Consumer<SQLException> onError) throws SQLException {
		return stream(query(sql), mapper, onError);
	}
	/**
	 * will throw runtime exception onError
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static <E> Stream<E> stream(ResultSet rs, SqlFunction<ResultSet, E> mapper, Consumer<SQLException> onError) throws SQLException {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(rs, mapper, onError), Spliterator.ORDERED | Spliterator.IMMUTABLE), false)
				.onClose(() -> {
					try {
						rs.close();
					} catch (SQLException e) {
						onError.accept(e);
					}
				});
	}
	public <E> Iterator<E> iterator(String sql, SqlFunction<ResultSet, E> mapper, Consumer<SQLException> onError) throws SQLException {
		return iterator(query(sql), mapper, onError);
	}
	public static <E> Iterator<E> iterator(ResultSet rs, SqlFunction<ResultSet, E> mapper, Consumer<SQLException> onError) throws SQLException {
		return new Iterator<E>() {
			Boolean next = null;
			{
				next = rs.next();
			}
			@Override
			public E next() {
				next = null;
				try {
					return mapper.apply(rs);
				} catch (SQLException e) {
					onError.accept(e);
					next = false;
				}
				return null;
			}
			@Override
			public boolean hasNext() {
				if(next == null) {
					try {
						next = rs.next();
					} catch (SQLException e) {
						onError.accept(e);
						next = false;
					}
				}
				return next;
			}
		};
	}
	public <E> Stream<E> stream(String sql, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return stream(sql,mapper,DEFAULT_ON_ERROR);
	}
	public static <E> Stream<E> stream(ResultSet rs, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return  stream(rs,mapper,DEFAULT_ON_ERROR);
	}
	public <E> Iterator<E> iterator(String sql, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return  iterator(sql,mapper,DEFAULT_ON_ERROR);
	}
	public static <E> Iterator<E> iterator(ResultSet rs, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return  iterator(rs,mapper,DEFAULT_ON_ERROR);
	}
	public static final Consumer<SQLException> DEFAULT_ON_ERROR = e -> {throw new RuntimeException(e);}; 

	public void createStatementBlock(SqlConsumer<Statement> consumer) throws SQLException {
		try(Statement s = connection.createStatement()) {
			consumer.accept(s);   
		}
	}
	public <E> E prepareStatementBlock(String sql, SqlFunction<PreparedStatement, E> action) throws SQLException {
		try(PreparedStatement s = prepareStatement(sql)) {
			return action.apply(s);   
		}
	}
	public <E> E findFirst(String sql, SqlFunction<ResultSet, E> mapper) throws SQLException{
		return executeQuery(sql, rs -> rs.next() ? mapper.apply(rs) : null); 
	}
	/**
	 * closed sql with ");\n"
	 * @param tableName
	 * @param columnNames
	 * @return
	 * @throws SQLException
	 */
	public static String insertSQL(String tableName, String...columnNames) {
		if(Checker.isEmpty(columnNames))
			throw new IllegalArgumentException("no column names specified");
		
		StringBuilder sb = new StringBuilder().append("INSERT INTO ").append(tableName).append("(");
		
		for (String s : columnNames) 
			sb.append(s).append(',');
		
		sb.setLength(sb.length() - 1);
		sb.append(") VALUES(");
		
		for (int i = 0; i < columnNames.length; i++)
			sb.append('?').append(',');			
		
		sb.setLength(sb.length() - 1);
		sb.append(");\n");
		
		return sb.toString();
	}
	/**
	 * not closed, can be appended
	 * @param tableName
	 * @param columnNames
	 * @return
	 * @throws SQLException
	 */
	public static StringBuilder selectSQL(String tableName, String...columnNames) {
		if(Checker.isEmpty(columnNames))
			throw new IllegalArgumentException("no column names specified");
		
		StringBuilder sb = new StringBuilder().append("SELECT ");
		
		for (String s : columnNames) 
			sb.append(s).append(',');
		sb.setLength(sb.length() - 1);
		
		sb.append(" FROM ").append(tableName);
		
		return sb;
	}
}
