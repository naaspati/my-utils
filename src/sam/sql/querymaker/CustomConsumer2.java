package sam.sql.querymaker;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface CustomConsumer2<E> {
	void accept(int n, PreparedStatement ps, E e) throws SQLException; 
}
