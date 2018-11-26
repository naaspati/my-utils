package sam.books;
import static sam.books.BooksMeta.*;
import static sam.books.BooksMeta.ID;
import static sam.books.BooksMeta.LOG_NUMBER;
import static sam.books.BooksMeta.PATH_TABLE_NAME;
import static sam.books.BooksMeta.TABLENAME;
import static sam.books.BooksMeta.DML_TYPE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import sam.sql.sqlite.SQLiteDB;
public class ChangeLog {
	public static enum Type {
		UPDATE, INSERT, DELETE 
	} 
	public final int log_number;
	public final Type dml_type;
	public final String table_name;
	public final int id;

	public ChangeLog(ResultSet rs) throws SQLException {
		this.log_number = rs.getInt(LOG_NUMBER);
		this.dml_type = Type.valueOf(rs.getString(DML_TYPE).toUpperCase());
		this.table_name = table(rs.getString(TABLENAME));
		this.id = rs.getInt(ID);
	}
	private String table(String tablename) {
		switch (tablename) {
			case BOOK_TABLE_NAME: return BOOK_TABLE_NAME;
			case PATH_TABLE_NAME: return PATH_TABLE_NAME;
			default:
				throw new IllegalArgumentException("unknown tablename: "+tablename);
		}
	}
	public static List<ChangeLog> getAllAfter(int nth, SQLiteDB db, boolean removeDuplicates) throws SQLException {
		List<ChangeLog> list = db.collectToList("SELECT * FROM "+CHANGE_LOG_TABLE_NAME+ " WHERE "+LOG_NUMBER+">"+nth, ChangeLog::new);
		if(!removeDuplicates || list.isEmpty()) return list;

		int n = 0;
		// transversing backword to preserve max nth
		// remove duplication updates
		while(true) {
			int index = list.size() - (n++);
			if(index <= 0)
				break;
			ChangeLog u = list.get(index);
			list.removeIf(t -> t != u && 
					t.id == u.id && 
					t.dml_type == u.dml_type && 
					t.table_name == u.table_name
					);
		}
		return list;
	}

}
