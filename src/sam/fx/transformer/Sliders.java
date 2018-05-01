package sam.fx.transformer;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

class Sliders extends VBox implements AbstractTransform {
	
	public Sliders(DoubleProperty[] properties, String[] names, double min, double max) {
		super(5);
		setPadding(new Insets(10));
		
		for (int i = 0; i < properties.length; i++) {
			Slider sl = slider(min, max, names[i]);
			getChildren().add(hbox(text(names[i]), textfield(sl), sl));
			bind(sl, properties[i]);
		}
	}

	@Override
	public String title() {
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public void change(Node old, Node _new) {
	}

}
