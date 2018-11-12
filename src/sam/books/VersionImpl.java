package sam.books;

import static sam.books.VersionMeta.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import sam.sql.sqlite.SQLiteDB;
import sam.collection.Iterable2;


public class VersionImpl {
	public final int book_id;
	public final int version;

	public VersionImpl(ResultSet rs) throws SQLException {
		this.book_id = rs.getInt(BOOK_ID);
		this.version = rs.getInt(VERSION);
	}
	public VersionImpl(int book_id, int version){
		this.book_id = book_id;
		this.version = version;
	}
	@Override
	public int hashCode() {
		return book_id;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		return book_id == ((VersionImpl) obj).book_id;  
	}
	public int getBookId(){ return this.book_id; }
	public int getVersion(){ return this.version; }

	public static final String SELECT_ALL_SQL = "SELECT * FROM "+TABLE_NAME;
	public static List<VersionImpl> getAll(SQLiteDB db) throws SQLException{
		return db.collectToList(SELECT_ALL_SQL, VersionImpl::new);
	}
	public static final String FIND_BY_BOOK_ID = SELECT_ALL_SQL+" WHERE "+BOOK_ID+"=";
	public static VersionImpl getByBookId(SQLiteDB db, int book_id) throws SQLException {
		return db.findFirst(FIND_BY_BOOK_ID+book_id, VersionImpl::new);
	}
}

