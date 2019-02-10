package sam.sql.querymaker;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementConsumer<E> {
	void accept(int index, PreparedStatement ps, E e) throws SQLException; 
}
