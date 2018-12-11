package sam.fx.helpers;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public interface FxCell {
	
	public static <E> Callback<ListView<E>, ListCell<E>> listCell(BiConsumer<ListCell<E>, E> consumer) {
		return c -> new ListCell<E>(){
			@Override
			protected void updateItem(E s, boolean empty) {
				super.updateItem(s, empty);
				if(empty || s == null) {
					setText(null);
					setTooltip(null);
				}
				else 
					consumer.accept(this, s);
			}
		};
	}
	
	public static <E> Callback<ListView<E>, ListCell<E>> listCell(Function<E, String> mapper) {
		return listCell((cell, e) -> cell.setText(mapper.apply(e)));
	}
	
	public static <E> Callback<TreeView<E>, TreeCell<E>> treeCell(BiConsumer<TreeCell<E>, E> consumer) {
		return c -> new TreeCell<E>(){
			@Override
			protected void updateItem(E s, boolean empty) {
				super.updateItem(s, empty);
				if(empty || s == null) {
					setText(null);
					setTooltip(null);
				}
				else 
					consumer.accept(this, s);
			}
		};
	}
	
	public static <E> Callback<TreeView<E>, TreeCell<E>> treeCell(Function<E, String> mapper) {
		return treeCell((cell, e) -> cell.setText(mapper.apply(e)));
	}

}
