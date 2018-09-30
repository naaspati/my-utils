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
	public void setNull(String columnName, int sqlType)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNull(n, sqlType));
	}

	public void setBoolean(String columnName, boolean x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBoolean(n, x));
	}

	public void setByte(String columnName, byte x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setByte(n, x));
	}
	public void setShort(String columnName, short x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setShort(n, x));
	}

	public void setInt(String columnName, int x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setInt(n, x));
	}

	public void setLong(String columnName, long x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setLong(n, x));
	}

	public void setFloat(String columnName, float x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setFloat(n, x));
	}

	public void setDouble(String columnName, double x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setDouble(n, x));
	}

	public void setBigDecimal(String columnName, BigDecimal x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBigDecimal(n, x));
	}

	public void setString(String columnName, String x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setString(n, x));
	}

	public void setBytes(String columnName, byte[] x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBytes(n, x));
	}

	public void setDate(String columnName, Date x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setDate(n, x));
	}

	public void setTime(String columnName, Time x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setTime(n, x));
	}

	public void setTimestamp(String columnName, Timestamp x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setTimestamp(n, x));
	}

	public void setAsciiStream(String columnName, InputStream x, int length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setAsciiStream(n, x, length));
	}

	public void setBinaryStream(String columnName, InputStream x, int length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBinaryStream(n, x, length));
	}

	public void setObject(String columnName, Object x, int targetSqlType)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x, targetSqlType));
	}

	public void setObject(String columnName, Object x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x));
	}

	public void setCharacterStream(String columnName, Reader reader, int length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setCharacterStream(n, reader, length));
	}

	public void setRef(String columnName, Ref x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setRef(n, x));
	}

	public void setBlob(String columnName, Blob x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBlob(n, x));
	}

	public void setClob(String columnName, Clob x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setClob(n, x));
	}

	public void setArray(String columnName, Array x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setArray(n, x));
	}

	public void setDate(String columnName, Date x, Calendar cal)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setDate(n, x, cal));
	}

	public void setTime(String columnName, Time x, Calendar cal)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setTime(n, x, cal));
	}

	public void setTimestamp(String columnName, Timestamp x, Calendar cal)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setTimestamp(n, x, cal));
	}

	public void setNull(String columnName, int sqlType, String typeName)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNull(n, sqlType, typeName));
	}

	public void setURL(String columnName, URL x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setURL(n, x));
	}

	public void setRowId(String columnName, RowId x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setRowId(n, x));
	}

	public void setNString(String columnName, String value)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNString(n, value));
	}

	public void setNCharacterStream(String columnName, Reader value, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNCharacterStream(n, value, length));
	}

	public void setNClob(String columnName, NClob value)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNClob(n, value));
	}

	public void setClob(String columnName, Reader reader, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setClob(n, reader, length));
	}

	public void setBlob(String columnName, InputStream inputStream, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBlob(n, inputStream, length));
	}

	public void setNClob(String columnName, Reader reader, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNClob(n, reader, length));
	}

	public void setSQLXML(String columnName, SQLXML xmlObject)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setSQLXML(n, xmlObject));
	}

	public void setObject(String columnName, Object x, int targetSqlType, int scaleOrLength)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x, targetSqlType, scaleOrLength));
	}

	public void setAsciiStream(String columnName, InputStream x, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setAsciiStream(n, x, length));
	}

	public void setBinaryStream(String columnName, InputStream x, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBinaryStream(n, x, length));
	}

	public void setCharacterStream(String columnName, Reader reader, long length)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setCharacterStream(n, reader, length));
	}
	public void setAsciiStream(String columnName, InputStream x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setAsciiStream(n, x));
	}

	public void setBinaryStream(String columnName, InputStream x)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBinaryStream(n, x));
	}

	public void setCharacterStream(String columnName, Reader reader)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setCharacterStream(n, reader));
	}

	public void setNCharacterStream(String columnName, Reader value)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNCharacterStream(n, value));
	}

	public void setClob(String columnName, Reader reader)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setClob(n, reader));
	}

	public void setBlob(String columnName, InputStream inputStream)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setBlob(n, inputStream));
	}

	public void setNClob(String columnName, Reader reader)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setNClob(n, reader));
	}

	public void setObject(String columnName, Object x, SQLType targetSqlType, int scaleOrLength)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x, targetSqlType, scaleOrLength));
	}

	public  void setObject(String columnName, Object x, SQLType targetSqlType)  {
		int n = pss.size()+1;
		this.columnNames.add(columnName);
		pss.add(ps -> ps.setObject(n, x, targetSqlType));
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
