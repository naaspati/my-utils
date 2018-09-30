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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class JDBCHelper implements AutoCloseable {
	private Statement defaultStatement;
	private final Connection connection;

	protected JDBCHelper(Connection connection) {
		this.connection = connection;
	}
	public Statement getDefaultStatement() {
		return defaultStatement;
	}
	public void createDefaultStatement() throws SQLException {
		if(defaultStatement != null)
			return;
		defaultStatement = connection.createStatement();
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
		return connection.prepareStatement(sql);
	}
	public int executeUpdate(String sql) throws SQLException {
		return getStatement().executeUpdate(sql); 
	}
	private Statement getStatement() throws SQLException {
		return defaultStatement == null ? createStatement() : defaultStatement;
	}
	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}
	public ResultSet executeQuery(String sql) throws SQLException {
		return getStatement().executeQuery(sql);
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
		try(ResultSet rs = getStatement().executeQuery(sql)) {
			return action.accept(rs);
		}
	}
	/**
	 * 
	 * <pre>
	 * try(ResultSet rs = statement.executeQuery(sql)) {
	 *      while(rs.next()) action.accept(rs);
	 * }
	 * </pre>
	 * 
	 * @param sql
	 * @param action
	 * @return 
	 * @throws SQLException
	 */

	public ResultSet rs(String sql) throws SQLException {
		return getStatement().executeQuery(sql);
	}

	public void iterate(String sql, SqlConsumer<ResultSet> action) throws SQLException {
		iterate(rs(sql), action);
	}
	public static void iterate(ResultSet rs, SqlConsumer<ResultSet> action) throws SQLException {
		try(ResultSet rs2 = rs) {
			while(rs.next()) action.accept(rs);
		}
	}
	public <C extends Collection<E>, E> C collect(String sql,C sink, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return collect(rs(sql), sink, mapper); 
	}
	public <E> ArrayList<E> collectToList(String sql, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return collect(rs(sql), new ArrayList<>(), mapper); 
	}
	public static <E> ArrayList<E> collectToList(ResultSet rs, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return collect(rs, new ArrayList<>(), mapper); 
	}
	public static <C extends Collection<E>, E> C collect(ResultSet rs0,C sink, SqlFunction<ResultSet, E> mapper) throws SQLException {
		try(ResultSet rs = rs0) {
			while(rs.next()) sink.add(mapper.accept(rs));
		}
		return sink;
	}

	public <K, V>  HashMap<K, V> collectToMap(String sql, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect(rs(sql), new HashMap<>(), keymapper, valuemapper);
	}
	public static <K, V>  HashMap<K, V> collectToMap(ResultSet rs, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect(rs, new HashMap<>(), keymapper, valuemapper);
	}

	public <M extends Map<K, V>, K, V>  M collect(String sql,M sink, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		return collect(rs(sql), sink, keymapper, valuemapper);
	}
	public static <M extends Map<K, V>, K, V>  M collect(ResultSet rs0,M sink, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
		try(ResultSet rs = rs0) {
			while(rs.next()) sink.put(keymapper.accept(rs), valuemapper.accept(rs));
		}
		return sink;
	}

	public <E> Stream<E> stream(String sql, SqlFunction<ResultSet, E> mapper, Consumer<SQLException> onError) throws SQLException {
		return stream(getStatement().executeQuery(sql), mapper, onError);
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
		return iterator(getStatement().executeQuery(sql), mapper, onError);
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
					return mapper.accept(rs);
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
		return stream(sql,mapper,JDBCHelper::throwruntime);
	}
	public static <E> Stream<E> stream(ResultSet rs, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return  stream(rs,mapper,JDBCHelper::throwruntime);
	}
	public <E> Iterator<E> iterator(String sql, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return  iterator(sql,mapper,JDBCHelper::throwruntime);
	}
	public static <E> Iterator<E> iterator(ResultSet rs, SqlFunction<ResultSet, E> mapper) throws SQLException {
		return  iterator(rs,mapper,JDBCHelper::throwruntime);
	}
	public static void throwruntime(SQLException e) {
		throw new RuntimeException(e);
	}
	public void createStatementBlock(SqlConsumer<Statement> consumer) throws SQLException {
		try(Statement s = connection.createStatement()) {
			consumer.accept(s);   
		}
	}
	public <E> E prepareStatementBlock(String sql, SqlFunction<PreparedStatement, E> action) throws SQLException {
		try(PreparedStatement s = connection.prepareStatement(sql)) {
			return action.accept(s);   
		}
	}


}
