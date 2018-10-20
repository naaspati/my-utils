package sam.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlBiFunction<S,T,E> {
  public E accept(S s, T t) throws SQLException;
}
