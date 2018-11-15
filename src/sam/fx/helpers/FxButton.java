package sam.fx.helpers;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public interface FxButton {
	
	public static Button button(String text) {
		return button(text, null); 
	}
	public static Button button(String text, EventHandler<ActionEvent> action) {
		return button(text, action, null); 
	}
	public static Button button(String text, EventHandler<ActionEvent> action, Consumer<Button> modifier) {
		Button b = new Button(text);
		if(action != null)
			b.setOnAction(action);
		if(modifier != null)
			modifier.accept(b);
		return b;
	} 

	public static ToggleGroup toggleGroup(Toggle... toggles) {
		return toggleGroup(null, null, toggles);
	}
	public static ToggleGroup toggleGroup(ChangeListener<? super Toggle> listener, Toggle... toggles) {
		return toggleGroup(null, listener, toggles);
	}
	public static ToggleGroup toggleGroup(Toggle selected, ChangeListener<? super Toggle> listener, Toggle... toggles) {
		Objects.requireNonNull(toggles);

		ToggleGroup grp = new ToggleGroup();
		grp.getToggles().addAll(toggles);
		if(listener != null)
			grp.selectedToggleProperty().addListener(listener);
		if(selected != null)
			grp.selectToggle(selected);
		return grp;
	}

}
