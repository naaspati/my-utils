package sam.fx.helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import sam.string.StringUtils;

public interface FxTable {

	/**
	 * <pre>
	 *  TableColumn<S, T> c = new TableColumn<>(columnTitle);
	 *  c.setCellValueFactory(cell -> new SimpleObjectProperty<>(mapper.apply(cell.getValue())));
	 *  return c;
	 * </pre>
	 * @param columnTitle
	 * @param mapper
	 * @return
	 */
	public static <S, T> TableColumn<S, T> column(String columnTitle, Function<S, T> mapper){
		TableColumn<S, T> c = new TableColumn<>(columnTitle);
		c.setCellValueFactory(cell -> new SimpleObjectProperty<>(mapper.apply(cell.getValue())));
		return c;
	}
	
	public static <S, T> TableColumn<S, T> column(String columnTitle, String field){
		TableColumn<S, T> c = new TableColumn<>(columnTitle);
		c.setCellValueFactory(new PropertyValueFactory<>(field));
		return c;
	}
	
	public static <S, T> TableColumn<S, T> withPrefWidth(TableColumn<S, T> col, int prefWith) {
		col.setPrefWidth(prefWith);
		return col;
	}
	public static <S, T> TableColumn<S, T> column3(String columnTitle, Callback<CellDataFeatures<S, T>, ObservableValue<T>> callback){
		TableColumn<S, T> c = new TableColumn<>(columnTitle);
		c.setCellValueFactory(callback);
		return c;
	}
	/**
	 * <pre>
	 *  TableColumn<S, T> c = new TableColumn<>(columnTitle);
	 *  c.setCellValueFactory(cell -> mapper.apply(cell.getValue()));
	 *  return c; 
	 *  </pre>
	 * 	
	 * @param columnTitle
	 * @param mapper
	 * @return
	 */
	public static <S, T, U extends ObservableValue<T>> TableColumn<S, T> column2(String columnTitle, Function<S, U> mapper) {
		TableColumn<S, T> c = new TableColumn<>(columnTitle);
		c.setCellValueFactory(val -> mapper.apply(val.getValue()));
		return c;
	}

	@SuppressWarnings("unchecked")
	public static <E> List<TableColumn<E, Object>> createColumns(Class<E> cls) {
		return Arrays.stream(cls.getDeclaredMethods())
				.filter(m -> m.getName().startsWith("get"))
				.filter(m -> m.getParameterCount() == 0)
				.filter(m -> m.getReturnType() != void.class)
				.map(m -> {
					TableColumn<E, Object> t = new TableColumn<>(StringUtils.splitCamelCase(m.getName().substring(3)));
					t.setCellValueFactory(c -> {
						try {
							Object o = m.invoke(c.getValue());
							return o instanceof ObservableValue ? (ObservableValue<Object>)o : new SimpleObjectProperty<Object>(o);	
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					});
					return t;
				}).collect(Collectors.toList());
	}

}
