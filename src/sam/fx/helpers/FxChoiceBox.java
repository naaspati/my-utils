package sam.fx.helpers;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import sam.myutils.Checker;

public interface FxChoiceBox {
	@SuppressWarnings("unchecked")
	public static <E> ChoiceBox<E> choiceBox(E[] values, Function<String, E> mapper, boolean includeFirstAsNull) {
		if(includeFirstAsNull) {
			E[] f = (E[]) Array.newInstance(values.getClass().getComponentType(), values.length + 1);
			System.arraycopy(values, 0, f, 1, values.length);
			values = f;
		}

		ChoiceBox<E> choice = new ChoiceBox<>(FXCollections.observableList(Arrays.asList(values)));

		choice.setConverter(new StringConverter<E>() {
			@Override
			public String toString(E g) {
				return g == null ? "" : g.toString();
			}
			@Override
			public E fromString(String string) {
				return Checker.isEmpty(string) ? null : mapper.apply(string);
			}
		});
		return choice;
	}  

}
