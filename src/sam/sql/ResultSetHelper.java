package sam.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

public class ResultSetHelper {
	public static SqlFunction<ResultSet, Object> getObject(String columnName){
		return rs -> rs.getObject(columnName);
	}
	public static SqlFunction<ResultSet, Boolean> getBoolean(String columnName){
		return rs -> rs.getBoolean(columnName);
	}
	public static SqlFunction<ResultSet, Byte> getByte(String columnName){
		return rs -> rs.getByte(columnName);
	}
	public static SqlFunction<ResultSet, Short> getShort(String columnName){
		return rs -> rs.getShort(columnName);
	}
	public static SqlFunction<ResultSet, Integer> getInt(String columnName){
		return rs -> rs.getInt(columnName);
	}
	public static SqlFunction<ResultSet, Long> getLong(String columnName){
		return rs -> rs.getLong(columnName);
	}
	public static SqlFunction<ResultSet, Float> getFloat(String columnName){
		return rs -> rs.getFloat(columnName);
	}
	public static SqlFunction<ResultSet, Double> getDouble(String columnName){
		return rs -> rs.getDouble(columnName);
	}
	public static SqlFunction<ResultSet, byte[]> getBytes(String columnName){
		return rs -> rs.getBytes(columnName);
	}
	public static SqlFunction<ResultSet, Array> getArray(String columnName){
		return rs -> rs.getArray(columnName);
	}
	public static SqlFunction<ResultSet, URL> getURL(String columnName){
		return rs -> rs.getURL(columnName);
	}
	public static SqlFunction<ResultSet, Ref> getRef(String columnName){
		return rs -> rs.getRef(columnName);
	}
	public static SqlFunction<ResultSet, Time> getTime(String columnName){
		return rs -> rs.getTime(columnName);
	}
	public static SqlFunction<ResultSet, Date> getDate(String columnName){
		return rs -> rs.getDate(columnName);
	}
	public static SqlFunction<ResultSet, InputStream> getAsciiStream(String columnName){
		return rs -> rs.getAsciiStream(columnName);
	}
	public static SqlFunction<ResultSet, BigDecimal> getBigDecimal(String columnName){
		return rs -> rs.getBigDecimal(columnName);
	}
	public static SqlFunction<ResultSet, InputStream> getBinaryStream(String columnName){
		return rs -> rs.getBinaryStream(columnName);
	}
	public static SqlFunction<ResultSet, Blob> getBlob(String columnName){
		return rs -> rs.getBlob(columnName);
	}
	public static SqlFunction<ResultSet, Reader> getCharacterStream(String columnName){
		return rs -> rs.getCharacterStream(columnName);
	}
	public static SqlFunction<ResultSet, Clob> getClob(String columnName){
		return rs -> rs.getClob(columnName);
	}
	public static SqlFunction<ResultSet, Reader> getNCharacterStream(String columnName){
		return rs -> rs.getNCharacterStream(columnName);
	}
	public static SqlFunction<ResultSet, NClob> getNClob(String columnName){
		return rs -> rs.getNClob(columnName);
	}
	public static SqlFunction<ResultSet, String> getNString(String columnName){
		return rs -> rs.getNString(columnName);
	}
	public static SqlFunction<ResultSet, RowId> getRowId(String columnName){
		return rs -> rs.getRowId(columnName);
	}
	public static SqlFunction<ResultSet, SQLXML> getSQLXML(String columnName){
		return rs -> rs.getSQLXML(columnName);
	}
	public static SqlFunction<ResultSet, Timestamp> getTimestamp(String columnName){
		return rs -> rs.getTimestamp(columnName);
	}
	public static SqlFunction<ResultSet, String> getString(String columnName){
		return rs -> rs.getString(columnName);
	}


}
