package sam.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlFunction<T,E> {
  public E apply(T t) throws SQLException;
}
