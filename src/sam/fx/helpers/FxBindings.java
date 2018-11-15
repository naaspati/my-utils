package sam.fx.helpers;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

public class FxBindings {
	public static <S, T> ObjectBinding<T> map(ObservableValue<S> o, Function<S, T> mapper) {
		return new ObjectBinding<T>() {
			{
				bind(Objects.requireNonNull(o));
			}
			@Override
			protected T computeValue() {
				return mapper.apply(o.getValue());
			}
		};
	}

}
