package sam.fx.helpers;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

public class FxBindings {
    public static BooleanBinding isEmptyTrimmed(ObservableStringValue ob) {
        return new BooleanBinding() {
            {
                bind(Objects.requireNonNull(ob));
            }
            @Override
            protected boolean computeValue() {
                return ob.get() == null || ob.get().trim().isEmpty();
            }
        };
    }
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
