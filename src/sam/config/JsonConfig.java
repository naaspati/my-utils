package sam.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONPointer;

import sam.string.StringSplitIterator;

public class JsonConfig {
	private final JSONObject json;

	public JsonConfig(JSONObject json) {
		this.json = json;
	}
	public JsonConfig() {
		this(new JSONObject());
	}
	
	@SuppressWarnings("unchecked")
	public <E> E getDeep(String pointer, E defaultValue) {
		return getDeep(pointer, defaultValue, o -> (E)o);
	}

	public <E> E getDeep(String pointer, E defaultValue, Function<Object, E> mapper) {
		Object o;
		if(pointer.indexOf('.') < 0)
			o = opt(pointer);
		else {
			Iterator<String> itr = new StringSplitIterator(pointer, '.');
			o = json.opt(itr.next());
			while (itr.hasNext() && o != null) {
				String key = itr.next();
				if(o instanceof JSONObject)
					o = ((JSONObject) o).opt(key);
				else if(o instanceof JSONArray)
					o = ((JSONArray) o).opt(Integer.parseInt(key));
				else 
					throw new JSONException("failed to walk json for pointer:"+pointer+", at ref: "+key+", returned: "+o);
			}
		}
		
		if(o == null)
			return defaultValue;
		
		return mapper.apply(o);
	}
	public Object get(String key) throws JSONException {
		return json.get(key);
	}
	public BigDecimal getBigDecimal(String key) throws JSONException {
		return json.getBigDecimal(key);
	}
	public BigInteger getBigInteger(String key) throws JSONException {
		return json.getBigInteger(key);
	}
	public boolean getBoolean(String key) throws JSONException {
		return json.getBoolean(key);
	}
	public double getDouble(String key) throws JSONException {
		return json.getDouble(key);
	}
	public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) throws JSONException {
		return json.getEnum(clazz, key);
	}
	public float getFloat(String key) throws JSONException {
		return json.getFloat(key);
	}
	public int getInt(String key) throws JSONException {
		return json.getInt(key);
	}
	public JSONArray getJSONArray(String key) throws JSONException {
		return json.getJSONArray(key);
	}
	public JSONObject getJSONObject(String key) throws JSONException {
		return json.getJSONObject(key);
	}
	public JsonConfig getConfig(String key) throws JSONException {
		return new JsonConfig(getJSONObject(key));
	}
	public long getLong(String key) throws JSONException {
		return json.getLong(key);
	}
	public Number getNumber(String key) throws JSONException {
		return json.getNumber(key);
	}
	public String getString(String key) throws JSONException {
		return json.getString(key);
	}
	public boolean has(String key) {
		return json.has(key);
	}
	public boolean isNull(String key) {
		return json.isNull(key);
	}
	public Set<String> keySet() {
		return json.keySet();
	}
	public Iterator<String> keys() {
		return json.keys();
	}
	public int length() {
		return json.length();
	}
	public JSONArray names() {
		return json.names();
	}
	public Object opt(String key) {
		return json.opt(key);
	}
	public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
		return json.optBigDecimal(key, defaultValue);
	}
	public BigInteger optBigInteger(String key, BigInteger defaultValue) {
		return json.optBigInteger(key, defaultValue);
	}
	public boolean optBoolean(String key, boolean defaultValue) {
		return json.optBoolean(key, defaultValue);
	}
	public boolean optBoolean(String key) {
		return json.optBoolean(key);
	}
	public double optDouble(String key, double defaultValue) {
		return json.optDouble(key, defaultValue);
	}
	public double optDouble(String key) {
		return json.optDouble(key);
	}
	public <E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue) {
		return json.optEnum(clazz, key, defaultValue);
	}
	public <E extends Enum<E>> E optEnum(Class<E> clazz, String key) {
		return json.optEnum(clazz, key);
	}
	public float optFloat(String key, float defaultValue) {
		return json.optFloat(key, defaultValue);
	}
	public float optFloat(String key) {
		return json.optFloat(key);
	}
	public int optInt(String key, int defaultValue) {
		return json.optInt(key, defaultValue);
	}
	public int optInt(String key) {
		return json.optInt(key);
	}
	public JSONArray optJSONArray(String key) {
		return json.optJSONArray(key);
	}
	public JSONObject optJSONObject(String key) {
		return json.optJSONObject(key);
	}
	public JsonConfig optConfig(String key, Supplier<JsonConfig> defaultValue) {
		JSONObject json = optJSONObject(key);
		if(json == null)
			return defaultValue == null ? null : defaultValue.get();
		
		return new JsonConfig(json);
	}
	public long optLong(String key, long defaultValue) {
		return json.optLong(key, defaultValue);
	}
	public long optLong(String key) {
		return json.optLong(key);
	}
	public Number optNumber(String key, Number defaultValue) {
		return json.optNumber(key, defaultValue);
	}
	public Number optNumber(String key) {
		return json.optNumber(key);
	}
	public Object optQuery(JSONPointer key) {
		return json.optQuery(key);
	}
	public Object optQuery(String jsonPointer) {
		return json.optQuery(jsonPointer);
	}
	public String optString(String key, String defaultValue) {
		return json.optString(key, defaultValue);
	}
	public String optString(String key) {
		return json.optString(key);
	}
	public Object query(JSONPointer jsonPointer) {
		return json.query(jsonPointer);
	}
	public Object query(String jsonPointer) {
		return json.query(jsonPointer);
	}
	@Override
	public String toString() {
		return json.toString();
	}
}
