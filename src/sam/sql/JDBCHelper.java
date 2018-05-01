package sam.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
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
     * @throws SQLException
     */
    public void iterate(String sql, SqlConsumer<ResultSet> action) throws SQLException {
        try(ResultSet rs = getStatement().executeQuery(sql)) {
            while(rs.next()) action.accept(rs);
        }
    }
    public <C extends Collection<E>, E> C collect(String sql,C sink, SqlFunction<ResultSet, E> mapper) throws SQLException {
        try(ResultSet rs = getStatement().executeQuery(sql)) {
            while(rs.next()) sink.add(mapper.accept(rs));
        }
        return sink;
    }
    public <M extends Map<K, V>, K, V>  M collect(String sql,M sink, SqlFunction<ResultSet, K> keymapper, SqlFunction<ResultSet, V> valuemapper) throws SQLException {
        try(ResultSet rs = getStatement().executeQuery(sql)) {
            while(rs.next()) sink.put(keymapper.accept(rs), valuemapper.accept(rs));
        }
        return sink;
    }
    /**
     * will throw runtime exception onError
     * @param sql
     * @return
     * @throws SQLException
     */
    public <E> Stream<E> stream(String sql, SqlFunction<ResultSet, E> mapper, Consumer<SQLException> onError) throws SQLException {
        ResultSet rs = getStatement().executeQuery(sql);
            
            Iterator<E> itr = new Iterator<E>() {
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
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(itr, Spliterator.ORDERED | Spliterator.IMMUTABLE), false)
                    .onClose(() -> {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            onError.accept(e);
                        }
                    });
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
