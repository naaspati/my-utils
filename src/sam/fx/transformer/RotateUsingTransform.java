package sam.fx.transformer;

import java.util.IdentityHashMap;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;

class RotateUsingTransform extends VBox implements AbstractTransform {
	private final Slider sRotationAxisX, sRotationAxisY,sRotationAxisZ, sPivotX, sPivotY,sPivotZ, sAngle;
	private final SimpleObjectProperty<Point3D> rotationAxisProperty = new SimpleObjectProperty<>();
	private final IdentityHashMap<Node, Rotate> map = new IdentityHashMap<>();
	private Rotate current;

	public RotateUsingTransform() {
		super(5);
		setPadding(new Insets(10));

		sAngle = slider(-360, 360, "rotation [-360,360]");
		add(hbox(text("Angle (in degree)"),textfield(sAngle)));
		add(sAngle);

		getChildren().add(text("Rotation Axis"));

		sRotationAxisX = addControls("x");
		sRotationAxisY = addControls("y");
		sRotationAxisZ = addControls("z");

		add(text("Pivot"));

		sPivotX = addControls("x");
		sPivotY = addControls("y");
		sPivotZ = addControls("z");

		rotationAxisProperty.bind(Bindings.createObjectBinding(() -> new Point3D(sRotationAxisX.valueProperty().get(), sRotationAxisY.valueProperty().get(), sRotationAxisZ.valueProperty().get()), sRotationAxisX.valueProperty(), sRotationAxisY.valueProperty(), sRotationAxisZ.valueProperty()));
	}

	private void add(Node n) {
		getChildren().add(n);
	}

	private Slider addControls(String string) {
		Slider sld = slider(-360, 360, "rotation axis "+string);
		HBox hb = hbox(text(string), textfield(sld), sld);
		hb.setPadding(new Insets(0, 0, 0, 10));
		add(hb);
		return sld; 
	}


	@Override
	public String title() { return "Rotation"; }
	@Override
	public String toString() {
		double angle = current.getAngle();

		double 	pivotX = current.getPivotX(),
				pivotY = current.getPivotY(),
				pivotZ = current.getPivotZ();

		Point3D pt = current.getAxis();
		
		double rx = pt.getX(),
				ry = pt.getY(),
				rz = pt.getZ();

		/*
		new Rotate()
		new Rotate(angle)
		new Rotate(angle, pivotX, pivotY)
		new Rotate(angle, pivotX, pivotY, pivotZ)

		new Rotate(angle, axis)
		new Rotate(angle, pivotX, pivotY, pivotZ, axis)
		 */

		if(rx == 0 && ry == 0 && rz == 1) {
			if(pivotX == 0 && pivotY == 0 && pivotZ == 0)
				return angle == 0 ? "new Rotate()" : String.format("new Rotate(%s)", number(angle));

			return String.format("new Rotate(%s, %s, %s"+(pivotZ == 0 ? ")" : ", %s)"), numbers(angle, pivotX, pivotY, pivotZ)); 
		}
		
		String axis = String.format("new Point3D(%s, %s, %s)", numbers(rx, ry, rz));
		
		if(pivotX == 0 && pivotY == 0 && pivotZ == 0)
			return String.format("new Rotate(%s, %s)", number(angle), axis);
		
		return String.format("new Rotate(%s, %s, %s, %s, "+axis+")", numbers(angle, pivotX, pivotY, pivotZ));
	}

	@Override
	public void change(Node old, Node _new) {
		if(current != null) {
			current.axisProperty().unbind();
			unbind(sAngle, current.angleProperty());
			unbind(sPivotX, current.pivotXProperty());
			unbind(sPivotY, current.pivotYProperty());
			unbind(sPivotZ, current.pivotZProperty());
		}

		current = map.get(_new);
		if(current == null) {
			current = new Rotate();
			_new.getTransforms().add(current);
			map.put(_new, current);
		}

		Point3D pt = current.getAxis();

		if(pt != null) {
			sRotationAxisX.setValue(pt.getX());
			sRotationAxisY.setValue(pt.getY());
			sRotationAxisZ.setValue(pt.getZ());    
		}

		bind(sAngle, current.angleProperty());
		bind(sPivotX, current.pivotXProperty());
		bind(sPivotY, current.pivotYProperty());
		bind(sPivotZ, current.pivotZProperty());
		current.axisProperty().bind(rotationAxisProperty);
	}
}
