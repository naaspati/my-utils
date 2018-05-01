package sam.fx.transformer;

import java.util.IdentityHashMap;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

class Scaling extends VBox implements AbstractTransform {
	private final Slider sX, sY,sZ, sPivotX, sPivotY,sPivotZ;
	private final IdentityHashMap<Node, Scale> map = new IdentityHashMap<>();
	private Scale current;

	public Scaling(double min, double max) {
		super(5);
		setPadding(new Insets(10));

		boolean toggle[] = {false};
		Function<String, Slider> addControls = string -> {
			Slider sld = slider(toggle[0] ? -360 : -1, toggle[0] ? 360 : 1, "rotation axis "+string);
			HBox hb = hbox(text(string), textfield(sld), sld);
			hb.setPadding(new Insets(0, 0, 0, 10));
			add(hb);
			return sld; 
		};

		add(text("Scale"));

		sX = addControls.apply("x");
		sY = addControls.apply("y");
		sZ = addControls.apply("z");

		add(text("Pivot"));

		toggle[0] = true;
		sPivotX = addControls.apply("x");
		sPivotY = addControls.apply("y");
		sPivotZ = addControls.apply("z");
	}

	private void add(Node n) {
		getChildren().add(n);
	}

	@Override
	public String title() { return "Scaling"; }
	@Override
	public String toString() {
		double 	x = current.getX(),
				y = current.getY(),
				z = current.getZ();

		double 	pivotX = current.getPivotX(),
				pivotY = current.getPivotY(),
				pivotZ = current.getPivotZ();

		/**
		 new Scale();
		 new Scale(x, y);
		 new Scale(x, y, z)
		 new Scale(x, y, pivotX, pivotY)
		 new Scale(x, y, z, pivotX, pivotY, pivotZ)
		 */

		if(pivotX == 0 && pivotY == 0 && pivotZ  == 0) {
			if(x == 1 && y == 1 && z == 1)
				return "new Scale()";
			if(z == 1)
				return String.format("new Scale(%s, %s)", numbers(x, y));
			
			return String.format("new Scale(%s, %s, %s)", numbers(x, y, z));
		}
		
		if(z == 1 && pivotZ == 0)
			return String.format("new Scale(%s, %s, %s, %s)", numbers(x, y, pivotX, pivotY));
		
		return String.format("new Scale(%s, %s, %s,%s, %s, %s)", numbers(x, y, z, pivotX, pivotY, pivotZ));
	}

	@Override
	public void change(Node old, Node _new) {
		if(current != null) {
			unbind(sX, current.xProperty());
			unbind(sY, current.yProperty());
			unbind(sZ, current.zProperty());
			
			unbind(sPivotX, current.pivotXProperty());
			unbind(sPivotY, current.pivotYProperty());
			unbind(sPivotZ, current.pivotZProperty());
		}

		current = map.get(_new);
		if(current == null) {
			current = new Scale();
			_new.getTransforms().add(current);
			map.put(_new, current);
		}
		
		bind(sX, current.xProperty());
		bind(sY, current.yProperty());
		bind(sZ, current.zProperty());

		bind(sPivotX, current.pivotXProperty());
		bind(sPivotY, current.pivotYProperty());
		bind(sPivotZ, current.pivotZProperty());
	}}
