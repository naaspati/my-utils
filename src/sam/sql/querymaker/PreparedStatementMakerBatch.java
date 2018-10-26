package sam.sql.querymaker;

import static sam.myutils.MyUtilsCheck.isEmptyTrimmed;

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
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import sam.collection.Iterables;
import sam.sql.sqlite.SQLiteDB;

public abstract class PreparedStatementMakerBatch<E> {
	private final ArrayList<CustomConsumer2<E>> pss;
	
	public PreparedStatementMakerBatch() {
		this.pss = new ArrayList<>();
	}
	public PreparedStatementMakerBatch(PreparedStatementMakerBatch<E> from) {
		this.pss = from.pss;
	}
	private void add(String columnName, CustomConsumer2<E> s) {
		if(isEmptyTrimmed(columnName))
			throw new IllegalArgumentException("invalid columnname: "+columnName);
		
		addColumn(columnName);
		pss.add(s);
	}
	protected abstract void addColumn(String columnName);

	public void setNull(String columnName, ToIntFunction<E> sqlType)  {
		add(columnName, (n, ps, e) -> ps.setNull(n, sqlType.applyAsInt(e)));
	}

	public void setBoolean(String columnName, Function<E, Boolean> x)  {
		add(columnName, (n, ps, e) -> ps.setBoolean(n, x.apply(e)));
	}

	public void setByte(String columnName, Function<E, Byte> x)  {
		add(columnName, (n, ps, e) -> ps.setByte(n, x.apply(e)));
	}
	public void setShort(String columnName, Function<E, Short> x)  {
		add(columnName, (n, ps, e) -> ps.setShort(n, x.apply(e)));
	}

	public void setInt(String columnName, ToIntFunction<E> x)  {
		add(columnName,(n, ps, e) -> ps.setInt(n, x.applyAsInt(e)));
	}

	public void setLong(String columnName, ToLongFunction<E> x)  {
		add(columnName,(n, ps, e) -> ps.setLong(n, x.applyAsLong(e)));
	}

	public void setFloat(String columnName, Function<E, Float> x)  {
		add(columnName, (n, ps, e) -> ps.setFloat(n, x.apply(e)));
	}

	public void setDouble(String columnName, ToDoubleFunction<E> x)  {
		add(columnName, (n, ps, e) -> ps.setDouble(n, x.applyAsDouble(e)));
	}

	public void setBigDecimal(String columnName, Function<E, BigDecimal> x)  {
		add(columnName, (n, ps, e) -> ps.setBigDecimal(n, x.apply(e)));
	}

	public void setString(String columnName, Function<E, String> x)  {
		add(columnName, (n, ps, e) -> ps.setString(n, x.apply(e)));

	}

	public void setBytes(String columnName, Function<E, byte[]> x)  {
		add(columnName, (n, ps, e) -> ps.setBytes(n, x.apply(e)));

	}

	public void setDate(String columnName, Function<E, Date> x)  {
		add(columnName, (n, ps, e) -> ps.setDate(n, x.apply(e)));

	}

	public void setTime(String columnName, Function<E, Time> x)  {
		add(columnName, (n, ps, e) -> ps.setTime(n, x.apply(e)));

	}

	public void setTimestamp(String columnName, Function<E, Timestamp> x)  {
		add(columnName, (n, ps, e) -> ps.setTimestamp(n, x.apply(e)));

	}

	public void setAsciiStream(String columnName, Function<E, InputStream> x, ToIntFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setAsciiStream(n, x.apply(e), length.applyAsInt(e)));

	}

	public void setBinaryStream(String columnName, Function<E, InputStream> x, ToIntFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setBinaryStream(n, x.apply(e), length.applyAsInt(e)));

	}

	public void setObject(String columnName, Function<E, Object> x, ToIntFunction<E> targetSqlType)  {
		add(columnName, (n,ps,e) -> ps.setObject(n, x.apply(e), targetSqlType.applyAsInt(e)));

	}

	public void setObject(String columnName, Function<E, Object> x)  {
		add(columnName, (n, ps, e) -> ps.setObject(n, x.apply(e)));

	}

	public void setCharacterStream(String columnName, Function<E, Reader> reader, ToIntFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setCharacterStream(n, reader.apply(e), length.applyAsInt(e)));

	}

	public void setRef(String columnName, Function<E, Ref> x)  {
		add(columnName, (n, ps, e) -> ps.setRef(n, x.apply(e)));

	}



	public void setArray(String columnName, Function<E, Array> x)  {
		add(columnName, (n, ps, e) -> ps.setArray(n, x.apply(e)));

	}

	public void setDate(String columnName, Function<E, Date> x, Function<E, Calendar> cal)  {
		add(columnName, (n,ps,e) -> ps.setDate(n, x.apply(e), cal.apply(e)));

	}

	public void setTime(String columnName, Function<E, Time> x, Function<E, Calendar> cal)  {
		add(columnName, (n,ps,e) -> ps.setTime(n, x.apply(e), cal.apply(e)));

	}

	public void setTimestamp(String columnName, Function<E, Timestamp> x, Function<E, Calendar> cal)  {
		add(columnName, (n,ps,e) -> ps.setTimestamp(n, x.apply(e), cal.apply(e)));

	}

	public void setNull(String columnName, ToIntFunction<E> sqlType, Function<E, String> typeName)  {
		add(columnName, (n,ps,e) -> ps.setNull(n, sqlType.applyAsInt(e), typeName.apply(e)));

	}

	public void setURL(String columnName, Function<E, URL> x)  {
		add(columnName, (n, ps, e) -> ps.setURL(n, x.apply(e)));

	}

	public void setRowId(String columnName, Function<E, RowId> x)  {
		add(columnName, (n, ps, e) -> ps.setRowId(n, x.apply(e)));

	}

	public void setNString(String columnName, Function<E, String> value)  {
		add(columnName, (n, ps, e) -> ps.setNString(n, value.apply(e)));

	}

	public void setNCharacterStream(String columnName, Function<E, Reader> value, ToLongFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setNCharacterStream(n, value.apply(e), length.applyAsLong(e)));

	}

	public void setNClobByReader(String columnName, Function<E, Reader> reader)  {
		add(columnName, (n, ps, e) -> ps.setNClob(n, reader.apply(e)));

	}

	public void setNClob(String columnName, Function<E, NClob> value)  {
		add(columnName, (n, ps, e) -> ps.setNClob(n, value.apply(e)));

	}

	public void setClob(String columnName,  Function<E, Reader> reader, ToLongFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setClob(n, reader.apply(e), length.applyAsLong(e)));

	}

	public void setBlob(String columnName, Function<E, InputStream> inputStream, ToLongFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setBlob(n, inputStream.apply(e), length.applyAsLong(e)));

	}

	public void setNClob(String columnName,  Function<E, Reader> reader, ToLongFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setNClob(n, reader.apply(e), length.applyAsLong(e)));

	}

	public void setSQLXML(String columnName, Function<E, SQLXML> xmlObject)  {
		add(columnName, (n, ps, e) -> ps.setSQLXML(n, xmlObject.apply(e)));

	}

	public void setObject(String columnName, Function<E, Object> x, ToIntFunction<E> targetSqlType, ToIntFunction<E> scaleOrLength)  {
		add(columnName, (n,ps,e) -> ps.setObject(n, x.apply(e), targetSqlType.applyAsInt(e), scaleOrLength.applyAsInt(e)));

	}

	public void setAsciiStream(String columnName, Function<E, InputStream> x, ToLongFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setAsciiStream(n, x.apply(e), length.applyAsLong(e)));

	}

	public void setBinaryStream(String columnName, Function<E, InputStream> x, ToLongFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setBinaryStream(n, x.apply(e), length.applyAsLong(e)));

	}

	public void setCharacterStream(String columnName,  Function<E, Reader> reader, ToLongFunction<E> length)  {
		add(columnName, (n,ps,e) -> ps.setCharacterStream(n, reader.apply(e), length.applyAsLong(e)));

	}
	public void setAsciiStream(String columnName, Function<E, InputStream> x)  {
		add(columnName, (n, ps, e) -> ps.setAsciiStream(n, x.apply(e)));

	}

	public void setBinaryStream(String columnName, Function<E, InputStream> x)  {
		add(columnName, (n, ps, e) -> ps.setBinaryStream(n, x.apply(e)));

	}

	public void setCharacterStream(String columnName, Function<E, Reader> reader)  {
		add(columnName, (n, ps, e) -> ps.setCharacterStream(n, reader.apply(e)));

	}

	public void setNCharacterStream(String columnName, Function<E, Reader> value)  {
		add(columnName, (n, ps, e) -> ps.setNCharacterStream(n, value.apply(e)));

	}

	public void setBlob(String columnName, Function<E, Blob> x)  {
		add(columnName, (n, ps, e) -> ps.setBlob(n, x.apply(e)));

	}

	public void setClob(String columnName, Function<E, Clob> x)  {
		add(columnName, (n, ps, e) -> ps.setClob(n, x.apply(e)));

	}

	public void setClobByReader(String columnName, Function<E, Reader> reader)  {
		add(columnName, (n, ps, e) -> ps.setClob(n, reader.apply(e)));

	}

	public void setBlobByInputStream(String columnName, Function<E, InputStream> inputStream)  {
		add(columnName, (n, ps, e) -> ps.setBlob(n, inputStream.apply(e)));

	}

	public void setObject(String columnName, Function<E, Object> x, Function<E, SQLType> targetSqlType, ToIntFunction<E> scaleOrLength)  {
		add(columnName, (n,ps,e) -> ps.setObject(n, x.apply(e), targetSqlType.apply(e), scaleOrLength.applyAsInt(e)));

	}

	public void setObject(String columnName, Function<E, Object> x, Function<E, SQLType> targetSqlType)  {
		add(columnName, (n,ps,e) -> ps.setObject(n, x.apply(e), targetSqlType.apply(e)));
	} 
	protected void check() {
		if(isEmpty())
			throw new IllegalStateException("empty preparedstatement");
	}
	public int execute(Connection connection, Iterable<E> data) throws SQLException  {
		check();
		return execute(connection.prepareStatement(toString()), data);
	}
	
	public int execute(SQLiteDB db, Iterable<E> data) throws SQLException  {
		return execute(db.prepareStatement(toString()), data);
	}
	private int execute(PreparedStatement prepareStatement, Iterable<E> data) throws SQLException  {
		return execute2(prepareStatement, data).length;
	}
	private int[] execute2(PreparedStatement prepareStatement, Iterable<E> data) throws SQLException  {
		Objects.requireNonNull(data);
		Iterator<E> itr = data.iterator();
		if(!itr.hasNext())
			return new int[0];

		try(PreparedStatement ps = prepareStatement){
			for (E e : Iterables.of(itr)) {
				int n = 1;
				for (CustomConsumer2<E> c : pss) 
					c.accept(n++, ps, e);
				
				ps.addBatch();
			}
			return ps.executeBatch();
		}
	}
	public boolean isEmpty() {
		return pss.isEmpty();
	}
}
