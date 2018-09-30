package sam.anime.entities;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sam.anime.db.AnimeDB;
import sam.collection.Iterators;
import sam.myutils.MyUtilsCheck;
import sam.sql.SqlFunction;

public class AnimeList<E> implements Iterable<E> {

	private boolean modified;
	private List<E> data;
	/**
	 * use this data is not loaded 
	 */
	private ArrayList<E> newData;
	private final int mal_id;
	private final String[] columnNames;
	private final String tableName;
	private final SqlFunction<ResultSet, E> mapper;

	public AnimeList(int mal_id) {
		this.mal_id = mal_id;
		columnNames = null;
		tableName  = null;
		mapper = null;
	}

	public AnimeList(int mal_id, String[] columnNames, String tableName, SqlFunction<ResultSet, E> mapper) {
		this.columnNames = columnNames;
		this.tableName = tableName;
		this.mapper = mapper;
		this.mal_id = mal_id;
	}
	public AnimeList(int mal_id, String columnName, String tableName, SqlFunction<ResultSet, E> mapper) {
		this(mal_id, new String[] {columnName}, tableName, mapper);
	}

	private List<E> unmodif;
	public List<E> get(AnimeDB db){
		if(unmodif != null) return unmodif;
		if(db == null || columnNames == null) return unmodif;

		data = new ArrayList<>();
		unmodif = Collections.unmodifiableList(data);
		db.loadList(mal_id, columnNames, tableName, mapper, data);

		if(newData != null) {
			for (E e : newData) add(e);
			newData = null;
		}
		return unmodif;
	}

	public void set(List<E> list) {
		modified = true;
		this.newData = null;
		this.data = new ArrayList<>();
		if(list != null) {
			for (E e : list) add(e);
		}
		unmodif = Collections.unmodifiableList(data);
	}
	public void add(E e) {
		if(e == null) return;

		if(this.data == null) {
			if(newData == null) {
				newData = new ArrayList<>();
				unmodif = Collections.unmodifiableList(newData);
			}
			add0(e);
			return;
		}

		add0(e);
	}
	
	private List<E> list() {
		return newData != null ? newData : data;
	}
	private void add0(E e) {
		List<E> list = list();
		String string = e instanceof String ? (String)e : null;

		if(list.stream().noneMatch(s -> string != null ? s.toString().equalsIgnoreCase(string) : s.equals(e))) {
			modified = true;
			list.add(e);
		}
	}

	public boolean remove(E e) {
		modified = true;
		String string = e instanceof String ? (String)e : null;
		return list().removeIf(s -> string != null ? s.toString().equalsIgnoreCase(string) : s.equals(e));
	}
	public void removeAll(List<E> list) {
		if(MyUtilsCheck.isEmpty(list))
			return;
		
		if(newData != null)
			newData.removeAll(list);
		if(data != null)
			data.removeAll(list);
		
		modified = true;
	}

	@Override
	public String toString() {
		return String.valueOf(data != null ? data : newData != null ? newData : null);
	}
	public boolean isModified() {
		return modified;
	}
	@Override
	public Iterator<E> iterator() {
		return list() == null ? Iterators.empty() : Collections.unmodifiableList(list()).iterator();
	}
	public boolean isEmpty() {
		return MyUtilsCheck.isEmpty(list());
	}
}
