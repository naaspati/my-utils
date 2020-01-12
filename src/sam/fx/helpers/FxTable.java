package sam.fx.helpers;

import java.util.function.Function;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class FxTable {

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
	public static <S, T, U extends ObservableValue<T>> TableColumn<S, T> column2(String columnTitle, Function<S, U> mapper){
		TableColumn<S, T> c = new TableColumn<>(columnTitle);
		c.setCellValueFactory(cell -> mapper.apply(cell.getValue()));
		return c;
	}
	/**
	 * <pre>
	 *  TableColumn<S, T> c = new TableColumn<>(columnTitle);
	 *  c.setCellValueFactory(new PropertyValueFactory<>(property));
	 *  return c;
	 *  </pre>
	 * @param columnTitle
	 * @param property
	 * @return
	 */
	public static <S, T> TableColumn<S, T> column2(String columnTitle, String property){
		TableColumn<S, T> c = new TableColumn<>(columnTitle);
		c.setCellValueFactory(new PropertyValueFactory<>(property));
		return c;
	}

}
