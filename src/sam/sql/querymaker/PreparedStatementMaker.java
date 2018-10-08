package sam.sql.querymaker;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import sam.sql.SqlConsumer;
import sam.sql.sqlite.SQLiteDB;

public abstract class PreparedStatementMaker {
	protected final String tableName;
	protected ArrayList<SqlConsumer<PreparedStatement>> pss = new ArrayList<>();
	protected ArrayList<String> columnNames = new ArrayList<>();

	protected  PreparedStatementMaker(String tableName) {
		this.tableName = tableName;
	}
	public PreparedStatementMaker setNull(String columnName, int sqlType)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNull(n, sqlType));
		return this;
	}

	public PreparedStatementMaker setBoolean(String columnName, boolean x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBoolean(n, x));
		return this;
	}

	public PreparedStatementMaker setByte(String columnName, byte x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setByte(n, x));
		return this;
	}
	public PreparedStatementMaker setShort(String columnName, short x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setShort(n, x));
		return this;
	}

	public PreparedStatementMaker setInt(String columnName, int x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setInt(n, x));
		return this;
	}

	public PreparedStatementMaker setLong(String columnName, long x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setLong(n, x));
		return this;
	}

	public PreparedStatementMaker setFloat(String columnName, float x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setFloat(n, x));
		return this;
	}

	public PreparedStatementMaker setDouble(String columnName, double x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setDouble(n, x));
		return this;
	}

	public PreparedStatementMaker setBigDecimal(String columnName, BigDecimal x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBigDecimal(n, x));
		return this;
	}

	public PreparedStatementMaker setString(String columnName, String x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setString(n, x));
		return this;
	}

	public PreparedStatementMaker setBytes(String columnName, byte[] x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBytes(n, x));
		return this;
	}

	public PreparedStatementMaker setDate(String columnName, Date x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setDate(n, x));
		return this;
	}

	public PreparedStatementMaker setTime(String columnName, Time x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setTime(n, x));
		return this;
	}

	public PreparedStatementMaker setTimestamp(String columnName, Timestamp x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setTimestamp(n, x));
		return this;
	}

	public PreparedStatementMaker setAsciiStream(String columnName, InputStream x, int length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setAsciiStream(n, x, length));
		return this;
	}

	public PreparedStatementMaker setBinaryStream(String columnName, InputStream x, int length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBinaryStream(n, x, length));
		return this;
	}

	public PreparedStatementMaker setObject(String columnName, Object x, int targetSqlType)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x, targetSqlType));
		return this;
	}

	public PreparedStatementMaker setObject(String columnName, Object x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x));
		return this;
	}

	public PreparedStatementMaker setCharacterStream(String columnName, Reader reader, int length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setCharacterStream(n, reader, length));
		return this;
	}

	public PreparedStatementMaker setRef(String columnName, Ref x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setRef(n, x));
		return this;
	}

	public PreparedStatementMaker setBlob(String columnName, Blob x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBlob(n, x));
		return this;
	}

	public PreparedStatementMaker setClob(String columnName, Clob x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setClob(n, x));
		return this;
	}

	public PreparedStatementMaker setArray(String columnName, Array x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setArray(n, x));
		return this;
	}

	public PreparedStatementMaker setDate(String columnName, Date x, Calendar cal)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setDate(n, x, cal));
		return this;
	}

	public PreparedStatementMaker setTime(String columnName, Time x, Calendar cal)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setTime(n, x, cal));
		return this;
	}

	public PreparedStatementMaker setTimestamp(String columnName, Timestamp x, Calendar cal)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setTimestamp(n, x, cal));
		return this;
	}

	public PreparedStatementMaker setNull(String columnName, int sqlType, String typeName)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNull(n, sqlType, typeName));
		return this;
	}

	public PreparedStatementMaker setURL(String columnName, URL x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setURL(n, x));
		return this;
	}

	public PreparedStatementMaker setRowId(String columnName, RowId x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setRowId(n, x));
		return this;
	}

	public PreparedStatementMaker setNString(String columnName, String value)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNString(n, value));
		return this;
	}

	public PreparedStatementMaker setNCharacterStream(String columnName, Reader value, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNCharacterStream(n, value, length));
		return this;
	}

	public PreparedStatementMaker setNClob(String columnName, NClob value)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNClob(n, value));
		return this;
	}

	public PreparedStatementMaker setClob(String columnName, Reader reader, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setClob(n, reader, length));
		return this;
	}

	public PreparedStatementMaker setBlob(String columnName, InputStream inputStream, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBlob(n, inputStream, length));
		return this;
	}

	public PreparedStatementMaker setNClob(String columnName, Reader reader, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNClob(n, reader, length));
		return this;
	}

	public PreparedStatementMaker setSQLXML(String columnName, SQLXML xmlObject)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setSQLXML(n, xmlObject));
		return this;
	}

	public PreparedStatementMaker setObject(String columnName, Object x, int targetSqlType, int scaleOrLength)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x, targetSqlType, scaleOrLength));
		return this;
	}

	public PreparedStatementMaker setAsciiStream(String columnName, InputStream x, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setAsciiStream(n, x, length));
		return this;
	}

	public PreparedStatementMaker setBinaryStream(String columnName, InputStream x, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBinaryStream(n, x, length));
		return this;
	}

	public PreparedStatementMaker setCharacterStream(String columnName, Reader reader, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setCharacterStream(n, reader, length));
		return this;
	}
	public PreparedStatementMaker setAsciiStream(String columnName, InputStream x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setAsciiStream(n, x));
		return this;
	}

	public PreparedStatementMaker setBinaryStream(String columnName, InputStream x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBinaryStream(n, x));
		return this;
	}

	public PreparedStatementMaker setCharacterStream(String columnName, Reader reader)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setCharacterStream(n, reader));
		return this;
	}

	public PreparedStatementMaker setNCharacterStream(String columnName, Reader value)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNCharacterStream(n, value));
		return this;
	}

	public PreparedStatementMaker setClob(String columnName, Reader reader)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setClob(n, reader));
		return this;
	}

	public PreparedStatementMaker setBlob(String columnName, InputStream inputStream)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBlob(n, inputStream));
		return this;
	}

	public PreparedStatementMaker setNClob(String columnName, Reader reader)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNClob(n, reader));
		return this;
	}

	public PreparedStatementMaker setObject(String columnName, Object x, SQLType targetSqlType, int scaleOrLength)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x, targetSqlType, scaleOrLength));
		return this;
	}

	public  PreparedStatementMaker setObject(String columnName, Object x, SQLType targetSqlType)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x, targetSqlType));
		return this;
	} 

	public boolean execute(Connection connection) throws SQLException  {
		return execute(connection.prepareStatement(toString()));
	}
	public boolean execute(SQLiteDB db) throws SQLException  {
		return execute(db.prepareStatement(toString()));
	}

	private boolean execute(PreparedStatement prepareStatement) throws SQLException  {
		try(PreparedStatement ps = prepareStatement){
			for (SqlConsumer<PreparedStatement> c : pss)
				c.accept(ps);

			return ps.execute();
		}
	}
	public boolean isEmpty() {
		return pss.isEmpty();
	}
}
