package sam.fx.transformer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

class RotateUsingMethod extends VBox implements AbstractTransform {
	private final Slider sax, say,saz, sangle;
	private final SimpleObjectProperty<Point3D> rotationAxisProperty = new SimpleObjectProperty<>();

	public RotateUsingMethod() {
		super(5);
		setPadding(new Insets(10));
		
		sangle = slider(-360, 360, "rotation [-360,360]");
		getChildren().add(hbox(text("Angle (in degree)"),textfield(sangle)));
		getChildren().add(sangle);

		getChildren().add(text("Rotation Axis"));

		sax = addControls("x");
		say = addControls("y");
		saz = addControls("z");
		
		rotationAxisProperty.bind(Bindings.createObjectBinding(() -> new Point3D(sax.valueProperty().get(), say.valueProperty().get(), saz.valueProperty().get()), sax.valueProperty(), say.valueProperty(), saz.valueProperty()));
	}

	private Slider addControls(String string) {
		Slider sld = slider(-360, 360, "rotation axis "+string);
		getChildren().add(hbox(text(string), textfield(sld), sld));
		return sld; 
	}


	@Override
	public String title() { return "Rotation (using methods)"; }
	@Override
	public String toString() {
		Point3D pt = rotationAxisProperty.get();
		return ".setRotate(value);\n"+
				".setRotationAxis(new Point3D("+pt.getX()+", "+pt.getY()+", "+pt.getZ()+"))";
	}

	@Override
	public void change(Node old, Node _new) {
		if(old != null) {
			unbind(sangle, old.rotateProperty());
			old.rotationAxisProperty().unbind();
		}

		Point3D pt = _new.getRotationAxis();
		if(pt != null) {
			sax.valueProperty().set(pt.getX());
			say.valueProperty().set(pt.getY());
			saz.valueProperty().set(pt.getZ());    
		}

		bind(sangle, _new.rotateProperty());
		_new.rotationAxisProperty().bind(rotationAxisProperty);
	}
}
