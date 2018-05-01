package sam.fx.transformer;

import java.util.IdentityHashMap;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Shear;

class Shearing extends VBox implements AbstractTransform {
	private final Slider sX, sY, sPivotX, sPivotY;
	private final IdentityHashMap<Node, Shear> map = new IdentityHashMap<>();
	private Shear current;

	public Shearing(double min, double max) {
		super(5);
		setPadding(new Insets(10));

		boolean toggle[] = {false};
		Function<String, Slider> addControls = string -> {
			Slider sld = slider(toggle[0] ? min : -1, toggle[0] ? max : 1, "rotation axis "+string);
			HBox hb = hbox(text(string), textfield(sld), sld);
			hb.setPadding(new Insets(0, 0, 0, 10));
			add(hb);
			return sld; 
		};

		add(text("Shear"));

		sX = addControls.apply("x");
		sY = addControls.apply("y");

		add(text("Pivot"));

		toggle[0] = true;
		sPivotX = addControls.apply("x");
		sPivotY = addControls.apply("y");
	}

	private void add(Node n) {
		getChildren().add(n);
	}

	@Override
	public String title() { return "Shearing"; }
	@Override
	public String toString() {
		double 	x = current.getX(),
				y = current.getY();

		double 	pivotX = current.getPivotX(),
				pivotY = current.getPivotY();

		/**
		new Shear()
		new Shear(x, y)
		new Shear(x, y, pivotX, pivotY)
		 */

		if(pivotX == 0 && pivotY == 0) {
			if(x == 0 && y == 0)
				return "new Shear()";

			return String.format("new Shear(%s, %s)", numbers(x, y));
		}

		return String.format("new Shear(%s, %s, %s, %s)", numbers(x, y, pivotX, pivotY));
	}

	@Override
	public void change(Node old, Node _new) {
		if(current != null) {
			unbind(sX, current.xProperty());
			unbind(sY, current.yProperty());

			unbind(sPivotX, current.pivotXProperty());
			unbind(sPivotY, current.pivotYProperty());
		}

		current = map.get(_new);
		if(current == null) {
			current = new Shear();
			_new.getTransforms().add(current);
			map.put(_new, current);
		}

		bind(sX, current.xProperty());
		bind(sY, current.yProperty());

		bind(sPivotX, current.pivotXProperty());
		bind(sPivotY, current.pivotYProperty());
	}}
