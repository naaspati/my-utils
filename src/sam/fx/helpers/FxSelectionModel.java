package sam.fx.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.control.MultipleSelectionModel;

public interface FxSelectionModel {
	public static <E> List<E> getCopyOfSelected(MultipleSelectionModel<E> model) {
		List<E> selected = model.getSelectedItems();
		if(selected.isEmpty())
			return Collections.emptyList();
		
		if(selected.size() == 1)
			return Collections.singletonList(selected.get(0));
		else
			return new ArrayList<>(selected);
	}

}
