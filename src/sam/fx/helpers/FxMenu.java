package sam.fx.helpers;

import java.util.function.Consumer;
import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.KeyCodeCombination;

public interface FxMenu {
	public static MenuItem menuitem(String label, EventHandler<ActionEvent> action) {
		return menuitem(label, null, action);
	}
	public static MenuItem menuitem(String label, EventHandler<ActionEvent> action, ObservableValue<? extends Boolean> disable) {
		return menuitem(label, null, action, disable);
	}
	public static MenuItem menuitem(String label, KeyCodeCombination accelerator, EventHandler<ActionEvent> action) {
		return menuitem(label, accelerator, action, null);
	}
	public static MenuItem menuitem(String label, KeyCodeCombination accelerator, EventHandler<ActionEvent> action, ObservableValue<? extends Boolean> disable) {
		return menuitem(new MenuItem(label), accelerator, action, disable);
	}

	public static RadioMenuItem radioMenuitem(String label, EventHandler<ActionEvent> action) {
		return radioMenuitem(label, null, action);
	}
	public static RadioMenuItem radioMenuitem(String label, EventHandler<ActionEvent> action, ObservableValue<? extends Boolean> disable) {
		return radioMenuitem(label, null, action, disable);
	}
	public static RadioMenuItem radioMenuitem(String label, KeyCodeCombination accelerator, EventHandler<ActionEvent> action) {
		return radioMenuitem(label, accelerator, action, null);
	}
	public static RadioMenuItem radioMenuitem(String label, KeyCodeCombination accelerator, EventHandler<ActionEvent> action, ObservableValue<? extends Boolean> disable) {
		return menuitem(new RadioMenuItem(label), accelerator, action, disable);
	}
	@SafeVarargs
	public static <E> Menu radioMenuItemGroup(String menuName, E selected, Consumer<E> action, E...values) {
		RadioMenuItem[] rbtns = Stream.of(values)
				.map(s -> {
					RadioMenuItem rb = new RadioMenuItem(s.toString().toLowerCase().replace('_', ' '));
					rb.setSelected(s == selected);
					rb.setOnAction(e -> action.accept(s));
					return rb;
				})
				.toArray(RadioMenuItem[]::new);

		FxToggleGroup.toggleGroup(null, rbtns);

		return new Menu(menuName, null, rbtns);
	}

	public static <E extends MenuItem> E menuitem(E menuItem, KeyCodeCombination accelerator, EventHandler<ActionEvent> action, ObservableValue<? extends Boolean> disable) {
		if(accelerator != null)
			menuItem.setAccelerator(accelerator);
		if(action != null)
			menuItem.setOnAction(action);
		if(disable != null)
			menuItem.disableProperty().bind(disable);

		return menuItem;
	}
}
