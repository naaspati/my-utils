package sam.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer<T> {
    public void accept(T t) throws SQLException;
}
