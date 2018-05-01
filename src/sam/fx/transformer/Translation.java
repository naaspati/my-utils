package sam.fx.transformer;

import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;


class Translation extends VBox implements AbstractTransform {
	private final Slider sx, sy, sz;
	
	public Translation(double min, double max) {
		super(5);
		setPadding(new Insets(10));
		
		Function<String, Slider> addControls = string -> {
			Slider sld = slider(min, max, "translate "+string);
			getChildren().add(hbox(text(string), textfield(sld), sld));
			return sld;
		} ;
		
		sx = addControls.apply("x");
		sy = addControls.apply("y");
		sz = addControls.apply("z");
	}
	

	@Override
	public String title() { return "Translation"; }
	
	@Override
	public String toString() {
		return "new Translate("+sx.getValue()+", "+sy.getValue()+", "+sz.getValue()+")";
	}
	@Override
	public void change(Node old, Node _new) {
		if(old != null) {
			unbind(sx, old.translateXProperty());
			unbind(sy, old.translateYProperty());
			unbind(sz, old.translateZProperty());
		}
		bind(sx, _new.translateXProperty());
		bind(sy, _new.translateYProperty());
		bind(sz, _new.translateZProperty());
	}
}
