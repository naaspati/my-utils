package sam.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlBiConsumer<S, T> {
    public void accept(S s, T t) throws SQLException;
}
