package sam.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlSupplier<T> {
	T get() throws SQLException;

}
